package rundeck.data.job

import com.dtolabs.rundeck.core.common.FrameworkServiceCapabilities
import com.dtolabs.rundeck.core.plugins.PluginProviderConfiguration
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import grails.util.Holders
import grails.validation.Validateable
import org.rundeck.app.data.model.v1.job.workflow.WorkflowStepData
import rundeck.data.validation.shared.SharedWorkflowStepConstraints
import rundeck.data.validation.validators.workflowstep.WorkflowStepValidatorFactory

@JsonIgnoreProperties(["errors"])
class RdWorkflowStep implements WorkflowStepData, PluginProviderConfiguration, Validateable {
    Long id
    RdWorkflowStep errorHandler;
    Boolean keepgoingOnSuccess;
    String description;
    Map<String, Object> configuration
    Boolean nodeStep;
    String pluginType;
    Map<String,Object> pluginConfig;

    static constraints = {
        importFrom SharedWorkflowStepConstraints
        id(nullable: true)
        errorHandler(nullable: true, validator: { val, obj, errors ->
            if(!val) return
            if(obj.nodeStep && !val.nodeStep) {
                errors.rejectValue("errorHandler",'WorkflowStep.errorHandler.nodeStep.invalid', [errors.nestedPath] as Object[], "Step {0}: Must have a Node Step as an Error Handler")
            }
            new WorkflowStepValidatorFactory(Holders.grailsApplication.mainContext.getBean(FrameworkServiceCapabilities)).createValidator("${errors.nestedPath}.errorHandler", val).validate(val, errors)
        })
    }

    @Override
    @JsonIgnore
    String getProvider() {
        return pluginType
    }

}
