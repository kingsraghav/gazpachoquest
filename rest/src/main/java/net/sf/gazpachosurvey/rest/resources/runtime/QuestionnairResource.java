package net.sf.gazpachosurvey.rest.resources.runtime;

import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import net.sf.gazpachosurvey.domain.core.Questionnair;
import net.sf.gazpachosurvey.dto.SurveyDTO;
import net.sf.gazpachosurvey.facades.SurveyAccessorFacade;
import net.sf.gazpachosurvey.rest.beans.QuestionnairDefinitionBean;
import net.sf.gazpachosurvey.types.Language;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Path("questionnair")
@Provider
public class QuestionnairResource {

    private static final Logger logger = LoggerFactory.getLogger(QuestionnairResource.class);

    @Autowired
    private SurveyAccessorFacade surveyAccessorFacade;

    public QuestionnairResource() {
    }

    @GET
    @RolesAllowed("respondent")
    @Path("browse")
    @Produces({ "application/json", MediaType.APPLICATION_JSON })
    public Response browse(@Context
    final SecurityContext context) {
        logger.debug("New petition received from {}", context.getUserPrincipal().getName());
        Questionnair respondent = (Questionnair) context.getUserPrincipal();

        Integer surveyId = respondent.getStudy().getSurvey().getId();

        logger.debug("Respondent {} retriving QuestionnairDefinition for surveyId = {}", respondent.getId(), surveyId);

        return Response.ok().build();
    }

    @GET
    @RolesAllowed("respondent")
    @Path("definition")
    @Produces({ "application/json", MediaType.APPLICATION_JSON })
    public Response getDefinition(@Context
    final SecurityContext context) {
        logger.debug("New petition received from {}", context.getUserPrincipal().getName());
        Questionnair respondent = (Questionnair) context.getUserPrincipal();
        Integer surveyId = respondent.getStudy().getSurvey().getId();

        logger.debug("Respondent {} retriving QuestionnairDefinition for surveyId = {}", respondent.getId(), surveyId);

        SurveyDTO survey = surveyAccessorFacade.findOneSurvey(surveyId);

        Set<Language> translationsSupported = surveyAccessorFacade.findSurveyTranslations(surveyId);

        QuestionnairDefinitionBean definition = QuestionnairDefinitionBean.with()
                .languageSettings(survey.getLanguageSettings()).language(survey.getLanguage())
                .translationsSupported(translationsSupported).build();

        return Response.ok(definition).build();
    }

}
