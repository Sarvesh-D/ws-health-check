package com.ds.ws.health.mail;

import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

/**
 * Utility class to for mail related operations
 * 
 * @author <a href="mailto:sarvesh.dubey@hotmail.com">Sarvesh Dubey</a>
 * @since 16/09/2016
 * @version 1.0
 */
@Component
public class MailUtils {

    @Autowired
    private VelocityEngine velocityEngine;

    /**
     * Transforms the given velocity template by replacing the place-holders
     * with the actual values
     * 
     * @param templateLocation
     *            location of velocity template to be used
     * @param templateParams
     *            Map containing keys as the template place-holder and value as
     *            the actual value to be replaced with
     * @return Transformed message
     */
    public String transformTemplateBody(String templateLocation, Map<String, Object> templateParams) {
	return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templateLocation, "UTF-8", templateParams);

    }
}
