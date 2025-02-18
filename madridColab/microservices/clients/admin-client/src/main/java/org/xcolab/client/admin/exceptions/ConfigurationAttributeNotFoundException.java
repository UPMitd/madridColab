package org.xcolab.client.admin.exceptions;

import org.xcolab.commons.attributes.exceptions.AttributeNotFoundException;

public class ConfigurationAttributeNotFoundException extends AttributeNotFoundException {

    public ConfigurationAttributeNotFoundException(String name) {
        super("ConfigurationAttribute " + name + " could not be found");
    }

}
