package org.opensrp.web.controller;

import ch.lambdaj.function.convert.Converter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.opensrp.common.AllConstants;
import org.opensrp.domain.*;
import org.opensrp.dto.*;
import org.opensrp.repository.*;
import org.opensrp.scheduler.SystemEvent;
import org.opensrp.scheduler.TaskSchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.collection.LambdaCollections.with;
import static java.text.MessageFormat.format;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class ServiceController {
    private static Logger logger = LoggerFactory.getLogger(ServiceController.class.toString());
    private ReferralServiceRepository referralServiceRepository;
    private IndicatorRepository indicatorRepository;
    private ReferralTypeRepository referralTypeRepository;
    private ServiceIndicatorRepository serviceIndicatorRepository;
    private ClientRegistrationReasonRepository registrationReasonRepository;
    private ReferralFeedbackRepository feedbackRepository;
    private TBTestTypeRepository tbTestTypeRepository;
    private TaskSchedulerService scheduler;

    @Autowired
    public ServiceController(ReferralServiceRepository referralServiceRepository, TaskSchedulerService scheduler, TBTestTypeRepository tbTestTypeRepository,
                             ServiceIndicatorRepository serviceIndicatorRepository, IndicatorRepository indicatorRepository, ReferralTypeRepository referralTypeRepository,ReferralFeedbackRepository feedbackRepository,ClientRegistrationReasonRepository registrationReasonRepository) {
        this.referralServiceRepository = referralServiceRepository;
        this.tbTestTypeRepository = tbTestTypeRepository;
        this.scheduler = scheduler;
        this.serviceIndicatorRepository = serviceIndicatorRepository;
        this.indicatorRepository = indicatorRepository;
        this.referralTypeRepository = referralTypeRepository;
        this.registrationReasonRepository = registrationReasonRepository;
        this.feedbackRepository = feedbackRepository;
    }

    @RequestMapping(headers = {"Accept=application/json"}, method = POST, value = "/add-referral-services")
    public ResponseEntity<HttpStatus> saveReferralServices(@RequestBody String json) {
        try {
	        List<ReferralServiceDTO> afyaServiceDTOS = new Gson().fromJson(json, new TypeToken<List<ReferralServiceDTO>>() {
	        }.getType());

            if (afyaServiceDTOS.isEmpty()) {
                return new ResponseEntity<>(BAD_REQUEST);
            }

            scheduler.notifyEvent(new SystemEvent<>(AllConstants.OpenSRPEvent.HEALTH_FACILITY_SUBMISSION, afyaServiceDTOS));


            List<ReferralService> referralServices =  with(afyaServiceDTOS).convert(new Converter<ReferralServiceDTO, ReferralService>() {
                @Override
                public ReferralService convert(ReferralServiceDTO boreshaAfyaServiceDTO) {
                    ReferralService referralService = new ReferralService();
                    referralService.setServiceName(boreshaAfyaServiceDTO.getServiceName());
                    referralService.setServiceNameSw(boreshaAfyaServiceDTO.getReferralServiceNameSw());
                    referralService.setCategoryName(boreshaAfyaServiceDTO.getCategory());
                    referralService.setActive(boreshaAfyaServiceDTO.isActive());
                    return referralService;
                }
            });



            for (ReferralService referralService : referralServices) {
                referralServiceRepository.save(referralService);
            }

            logger.debug(format("Saved Boresha Afya Service to queue.\nSubmissions: {0}", afyaServiceDTOS));
        } catch (Exception e) {
        	e.printStackTrace();
            logger.error(format("Boresha Afya Service processing failed with exception {0}.\nSubmissions: {1}", e, json));
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(CREATED);
    }

    @RequestMapping(headers = {"Accept=application/json"}, method = POST, value = "/add-referral-indicators")
    public ResponseEntity<HttpStatus> saveReferralIndicators(@RequestBody String json) {
        try {
            List<IndicatorDTO> indicatorDTOS = new Gson().fromJson(json, new TypeToken<List<IndicatorDTO>>() {
            }.getType());

            if (indicatorDTOS.isEmpty()) {
                return new ResponseEntity<>(BAD_REQUEST);
            }

            scheduler.notifyEvent(new SystemEvent<>(AllConstants.OpenSRPEvent.HEALTH_FACILITY_SUBMISSION, indicatorDTOS));

            List<Indicator> indicators =  with(indicatorDTOS).convert(new Converter<IndicatorDTO, Indicator>() {
                @Override
                public Indicator convert(IndicatorDTO referralIndicatorDTO) {
                    Indicator indicator = new Indicator();
                    indicator.setIndicatorName(referralIndicatorDTO.getIndicatorName());
                    indicator.setIndicatorNameSw(referralIndicatorDTO.getIndicatorNameSw());
                    indicator.setActive(referralIndicatorDTO.isActive());
                    return indicator;
                }
            });

            Exception exp = null;
            for (Indicator indicator : indicators) {
                try {
                    indicatorRepository.save(indicator);
                }catch (Exception e){
                    exp=e;
                    e.printStackTrace();
                }
            }

            if(exp!=null)
                throw  exp;

            logger.debug(format("Saved Referral Indicator to queue.\nSubmissions: {0}", indicatorDTOS));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(format("Referral Indicators processing failed with exception {0}.\nSubmissions: {1}", e, json));
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(CREATED);
    }

    @RequestMapping(headers = {"Accept=application/json"}, method = POST, value = "/add-referral-types")
    public ResponseEntity<HttpStatus> saveReferralType(@RequestBody String json) {
        try {
            List<ReferralTypeDTO> referralTypeDTOS = new Gson().fromJson(json, new TypeToken<List<ReferralTypeDTO>>() {
            }.getType());

            if (referralTypeDTOS.isEmpty()) {
                return new ResponseEntity<>(BAD_REQUEST);
            }

            scheduler.notifyEvent(new SystemEvent<>(AllConstants.OpenSRPEvent.HEALTH_FACILITY_SUBMISSION, referralTypeDTOS));

            List<ReferralType> referralTypes =  with(referralTypeDTOS).convert(new Converter<ReferralTypeDTO, ReferralType>() {
                @Override
                public ReferralType convert(ReferralTypeDTO referralTypeDTO) {
                    ReferralType referralType = new ReferralType();
                    referralType.setReferralTypeName(referralTypeDTO.getReferralTypeName());
                    referralType.setActive(referralTypeDTO.isActive());
                    return referralType;
                }
            });

            Exception exp = null;
            for (ReferralType referralType : referralTypes) {
                try {
                    referralTypeRepository.save(referralType);
                }catch (Exception e){
                    exp=e;
                    e.printStackTrace();
                }
            }

            if(exp!=null)
                throw  exp;

            logger.debug(format("Saved Referral Type to queue.\nSubmissions: {0}", referralTypeDTOS));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(format("Referral Types processing failed with exception {0}.\nSubmissions: {1}", e, json));
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(CREATED);
    }

    @RequestMapping(headers = {"Accept=application/json"}, method = POST, value = "/add-referral-services-indicators")
    public ResponseEntity<HttpStatus> saveReferralServiceIndicators(@RequestBody String json) {
        try {
            List<ReferralServiceIndicatorDTO> referralServiceIndicators = new Gson().fromJson(json, new TypeToken<List<ReferralServiceIndicatorDTO>>() {
            }.getType());

            if (referralServiceIndicators.isEmpty()) {
                return new ResponseEntity<>(BAD_REQUEST);
            }

            scheduler.notifyEvent(new SystemEvent<>(AllConstants.OpenSRPEvent.HEALTH_FACILITY_SUBMISSION, referralServiceIndicators));


            List<List<ServiceIndicator>> referralIndicatorsList =  with(referralServiceIndicators).convert(new Converter<ReferralServiceIndicatorDTO, List<ServiceIndicator>>() {
                @Override
                public List<ServiceIndicator> convert(ReferralServiceIndicatorDTO referralServiceIndicatorDTO) {
                    List<ServiceIndicator> referralIndicators = new ArrayList<>();

                    for (Long indicatorId:referralServiceIndicatorDTO.getReferralIndicatorId()) {

                        ServiceIndicator serviceIndicator = new ServiceIndicator();

                        Indicator indicator = new Indicator();
                        indicator.setIndicatorId(indicatorId);

                        PKReferralServiceIndicator pkReferralServiceIndicator = new PKReferralServiceIndicator(referralServiceIndicatorDTO.getReferralServiceId(), indicatorId);
                        serviceIndicator.setPkReferralServiceIndicator(pkReferralServiceIndicator);

                        referralIndicators.add(serviceIndicator);
                    }


                    return referralIndicators;
                }
            });

            long id = 1;
            List<ServiceIndicator> indicators =  serviceIndicatorRepository.getReferralServicesIndicators("SELECT * FROM "+ ServiceIndicator.tbName+" ORDER BY "+ ServiceIndicator.COL_SERVICE_ID+" LIMIT 1",null);
            if(indicators.size()>0){
                id = indicators.get(0).getServiceIndicatorId()+1;
            }

            Exception exp = null;
            for (List<ServiceIndicator> serviceIndicatorsList : referralIndicatorsList) {
                String indicatorIds = "";
                long serviceId = 0;
                for (ServiceIndicator serviceIndicator : serviceIndicatorsList) {
                    try {
                        indicatorIds+= serviceIndicator.getPkReferralServiceIndicator().getIndicatorId();
                        serviceId = serviceIndicator.getPkReferralServiceIndicator().getServiceId();

                        serviceIndicator.setServiceIndicatorId(id);
                        id++;
                        serviceIndicatorRepository.save(serviceIndicator);
                    }catch (Exception e){
                        exp = e;
                        e.printStackTrace();
                    }
                }

                //TODO COZE reimplement this
//                referralServiceIndicatorRepository.executeQuery("DELETE FROM "+ReferralServiceIndicator.tbName+" WHERE "+ReferralServiceIndicator.COL_SERVICE_ID+" = "+serviceId + " AND "+ReferralServiceIndicator.COL_INDICATOR_ID+" NOT IN ("+indicatorIds+")");
            }

            if(exp!=null)
                throw  exp;
            logger.debug(format("Saved Referral Indicator to queue.\nSubmissions: {0}", referralServiceIndicators));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(format("Referral Indicators processing failed with exception {0}.\nSubmissions: {1}", e, json));
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(CREATED);
    }

    @RequestMapping(headers = {"Accept=application/json"}, method = GET, value = "/boresha-afya-services")
    @ResponseBody
    public List<ReferralServiceIndicatorsDTO> getBoreshaAfyaServices() {

        List<ReferralService> allReferralServices = null;
        try {
            allReferralServices = referralServiceRepository.getReferralServices("Select * from "+ ReferralService.tbName,null);
        } catch (Exception e) {
            e.printStackTrace();
        }


        List<ReferralServiceIndicatorsDTO> referralServiceIndicatorsDTOS = new ArrayList<>();
        for(ReferralService referralService:allReferralServices) {
            ReferralServiceIndicatorsDTO referralServiceIndicatorsDTO = new ReferralServiceIndicatorsDTO();

            referralServiceIndicatorsDTO.setCategory(referralService.getCategoryName());
            referralServiceIndicatorsDTO.setServiceId(referralService.getServiceId());
            referralServiceIndicatorsDTO.setServiceName(referralService.getServiceName());
            referralServiceIndicatorsDTO.setServiceNameSw(referralService.getServiceNameSw());
            referralServiceIndicatorsDTO.setActive(referralService.isActive());


            List<ServiceIndicator> serviceIndicators = null;
            try {
                Object[] objects = new Object[]{
                        referralService.getServiceId()
                };
                serviceIndicators =
                        serviceIndicatorRepository.getReferralServicesIndicators("SELECT * FROM " + ServiceIndicator.tbName+" WHERE "+ ServiceIndicator.COL_SERVICE_ID +" =?", objects);
            } catch (Exception e) {
                e.printStackTrace();
            }

            List<IndicatorDTO> indicatorDTOS = new ArrayList<>();
            for(ServiceIndicator serviceIndicator: serviceIndicators){
                IndicatorDTO indicatorDTO = new IndicatorDTO();
                indicatorDTO.setReferralServiceIndicatorId(serviceIndicator.getServiceIndicatorId());

                Object[] objects = new Object[]{
                        serviceIndicator.getPkReferralServiceIndicator().getIndicatorId()
                };
                List<Indicator> indicators = null;
                try {
                    indicators = indicatorRepository.getReferralIndicators("SELECT * FROM "+ Indicator.tbName+" WHERE "+ Indicator.COL_INDICATOR_ID +" =?",objects);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(indicators.size()>0) {
                    indicatorDTO.setIndicatorName(indicators.get(0).getIndicatorName());
                    indicatorDTO.setIndicatorNameSw(indicators.get(0).getIndicatorNameSw());
                    indicatorDTO.setReferralIndicatorId(indicators.get(0).getIndicatorId());
                    indicatorDTO.setActive(indicators.get(0).isActive());
                }

                indicatorDTOS.add(indicatorDTO);

            }
            referralServiceIndicatorsDTO.setIndicators(indicatorDTOS);

            referralServiceIndicatorsDTOS.add(referralServiceIndicatorsDTO);
        }




        return referralServiceIndicatorsDTOS;
    }

    @RequestMapping("delete-referral-service-indicator/{referralServiceIndicatorId}")
    @ResponseBody
    private ResponseEntity<ServiceIndicator>  deleteReferralServiceIndicatorId(@PathVariable("referralServiceIndicatorId") String referralServiceIndicatorId) {
        if(referralServiceIndicatorId.equals("")){
            return new ResponseEntity<ServiceIndicator>(BAD_REQUEST);
        }

        try {
            serviceIndicatorRepository.executeQuery("DELETE  FROM "+ ServiceIndicator.tbName+" WHERE "+ ServiceIndicator.COL_SERVICE_INDICATOR_ID +" = "+referralServiceIndicatorId);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<ServiceIndicator>(NOT_FOUND);
        }
        return new ResponseEntity<ServiceIndicator>(OK);
    }

	@RequestMapping(headers = {"Accept=application/json"}, method = GET, value = "/referral-types")
	@ResponseBody
	public List<ReferralType> getReferralTypes() {

		List<ReferralType> allReferralType = null;
		try {
			allReferralType = referralTypeRepository.getReferralType("Select * from "+ ReferralType.tbName,null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return allReferralType;
	}

    @RequestMapping(headers = {"Accept=application/json"}, method = GET, value = "/get-referral-services")
    @ResponseBody
    public List<ReferralService> getReferralServices() {

        List<ReferralService> referralServices = null;
        try {
            referralServices = referralServiceRepository.getReferralServices("Select * from "+ ReferralService.tbName,null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return referralServices;
    }


    @RequestMapping("get-referral-service/{serviceId}")
    @ResponseBody
    private ResponseEntity<ReferralService>  getReferralService(@PathVariable("serviceId") String serviceId) {
        if(serviceId.equals("")){
            return new ResponseEntity<ReferralService>(BAD_REQUEST);
        }
        ReferralService referralServices;
        Object[] arg = new Object[]{
                serviceId
        };
        try {
             referralServices = referralServiceRepository.getReferralServices("Select * from "+ ReferralService.tbName+" WHERE "+ReferralService.COL_SERVICE_ID +" =?",arg).get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<ReferralService>(NOT_FOUND);
        }
        return new ResponseEntity<ReferralService>(referralServices,OK);
    }

    @RequestMapping("delete-referral-service/{serviceId}")
    @ResponseBody
    private ResponseEntity<ReferralService>  deleteReferralService(@PathVariable("serviceId") String serviceId) {
        if(serviceId.equals("")){
            return new ResponseEntity<ReferralService>(BAD_REQUEST);
        }

        try {
            referralServiceRepository.executeQuery("DELETE  FROM "+ ReferralService.tbName+" WHERE "+ReferralService.COL_SERVICE_ID +" = "+serviceId);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<ReferralService>(NOT_FOUND);
        }
        return new ResponseEntity<ReferralService>(OK);
    }


    @RequestMapping(headers = {"Accept=application/json"}, method = GET, value = "/get-indicators")
    @ResponseBody
    public List<Indicator> getIndicators() {

        List<Indicator> indicators = null;
        try {
            indicators = indicatorRepository.getReferralIndicators("Select * from "+ Indicator.tbName,null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return indicators;
    }


    @RequestMapping("get-indicator/{indicatorId}")
    @ResponseBody
    private ResponseEntity<Indicator>  getIndicator(@PathVariable("indicatorId") String indicatorId) {
        if(indicatorId.equals("")){
            return new ResponseEntity<Indicator>(BAD_REQUEST);
        }
        Indicator indicator;
        Object[] arg = new Object[]{
                indicatorId
        };
        try {
            indicator = indicatorRepository.getReferralIndicators("Select * from "+ Indicator.tbName+" WHERE "+Indicator.COL_INDICATOR_ID +" =?",arg).get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<Indicator>(NOT_FOUND);
        }
        return new ResponseEntity<Indicator>(indicator,OK);
    }

    @RequestMapping("delete-indicator/{indicatorId}")
    @ResponseBody
    private ResponseEntity<Indicator>  deleteIndicator(@PathVariable("indicatorId") String indicatorId) {
        if(indicatorId.equals("")){
            return new ResponseEntity<Indicator>(BAD_REQUEST);
        }

        try {
            indicatorRepository.executeQuery("DELETE  FROM "+ Indicator.tbName+" WHERE "+Indicator.COL_INDICATOR_ID +" = "+indicatorId);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<Indicator>(NOT_FOUND);
        }
        return new ResponseEntity<Indicator>(OK);
    }

    @RequestMapping(headers = {"Accept=application/json"}, method = POST, value = "/save-tb-patient-type")
    public ResponseEntity<HttpStatus> savePatientType(@RequestBody List<TBPatientTypesDTO> tbPatientTypesDTOS) {
        try {
            if (tbPatientTypesDTOS.isEmpty()) {
                return new ResponseEntity<>(BAD_REQUEST);
            }

            scheduler.notifyEvent(new SystemEvent<>(AllConstants.OpenSRPEvent.HEALTH_FACILITY_SUBMISSION, tbPatientTypesDTOS));

            String json = new Gson().toJson(tbPatientTypesDTOS);
            List<TBPatientTypesDTO> tbPatientTypesDTOS1 = new Gson().fromJson(json, new TypeToken<List<TBPatientTypesDTO>>() {
            }.getType());

            List<TBTestType> tbTestTypes =  with(tbPatientTypesDTOS1).convert(new Converter<TBPatientTypesDTO, TBTestType>() {
                @Override
                public TBTestType convert(TBPatientTypesDTO tbPatientTypesDTO) {
                    TBTestType tbTestType = new TBTestType();
                    tbTestType.setTestTypeName(tbPatientTypesDTO.getPatientTypeName());
                    tbTestType.setIsActive(tbPatientTypesDTO.isActive());
                    return tbTestType;
                }
            });

            for (TBTestType tbTestType : tbTestTypes) {
                tbTestTypeRepository.save(tbTestType);
            }

            logger.debug(format("Saved TB Patient types to queue.\nSubmissions: {0}", tbPatientTypesDTOS));
        } catch (Exception e) {
            logger.error(format("TB Patient Types processing failed with exception {0}.\nSubmissions: {1}", e, tbPatientTypesDTOS));
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(CREATED);
    }

    @RequestMapping(headers = {"Accept=application/json"}, method = GET, value = "/tb-patient-test-types")
    @ResponseBody
    public List<TBPatientTypesDTO> getTBPatientTestTypes() {

        List<TBTestType> tbTestTypes = null;
        try {
            tbTestTypes = tbTestTypeRepository.getTBPatientTypes("Select * from "+ TBTestType.tbName,null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return with(tbTestTypes).convert(new Converter<TBTestType, TBPatientTypesDTO>() {
            @Override
            public TBPatientTypesDTO convert(TBTestType tbTestType) {
                return new TBPatientTypesDTO(tbTestType.getId(), tbTestType.getTestTypeName(), tbTestType.getIsActive());
            }
        });
    }

    @RequestMapping(headers = {"Accept=application/json"}, method = GET, value = "/referral-feedback")
    @ResponseBody
    public List<ReferralFeedback> getReferralFeedback() {

        List<ReferralFeedback> referralFeedbacks = null;
        try {
            referralFeedbacks = feedbackRepository.getReferralFeedback("SELECT * FROM "+ ReferralFeedback.tbName,null);
        } catch (Exception e) {
            e.printStackTrace();
        }

       return referralFeedbacks;
    }

    @RequestMapping(headers = {"Accept=application/json"}, method = GET, value = "/registration-reasons")
    @ResponseBody
    public List<ClientRegistrationReason> getRegistrationReasons() {

        List<ClientRegistrationReason> clientRegistrationReasons = null;
        try {
            clientRegistrationReasons = registrationReasonRepository.geRegistrationReasons("SELECT * FROM "+ ClientRegistrationReason.tbName,null);
        } catch (Exception e) {
            e.printStackTrace();
        }

       return clientRegistrationReasons;
    }
}
