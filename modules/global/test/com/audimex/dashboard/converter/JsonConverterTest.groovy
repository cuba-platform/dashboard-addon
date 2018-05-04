/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.converter

import com.audimex.dashboard.model.Parameter
import com.audimex.dashboard.model.param_value_types.EntityValue
import com.audimex.dashboard.model.param_value_types.EnumValue
import com.audimex.dashboard.model.param_value_types.ListEntitiesValue
import spock.lang.Specification

class JsonConverterTest extends Specification {
    JsonConverter converter

    //todo add more tests with json files
    void setup() {
        converter = new JsonConverter()
    }

    def "test serialisation parameters"() {
        when:
        String json = converter.widgetToJson(parameter)

        then:
        result.stream().allMatch({ r -> json.contains(r as CharSequence) })

        where:
        parameter               | result
        baseParameter()         | ['"name":"name"', '"alias":"alias"', '"mappedAlias":"mappedAlias"', '"orderNum":1']
        entityParameter()       | ['"entityId":"entityId"', '"metaClassName":"sec$User"', '"viewName":"_local"']
        enumStringParameter()   | ['"enumClassName":"someEnumClass"', '"enumValue":"someValue"']
        listEntitiesParameter() | ['"entityId":"entity1"', '"entityId":"entity2"']
    }

    def "test deserialization parameters"() {
        given:
        String json = "{\"name\":\"name\",\"alias\":\"alias\",\"mappedAlias\":\"mappedAlias\",\"orderNum\":1,\"value\":{\"className\":\"com.audimex.dashboard.model.param_value_types.EntityValue\",\"data\":{\"metaClassName\":\'sec\$User\',\"entityId\":\"entityId\",\"viewName\":\"_local\"}},\"id\":\"858d70c8-a536-6a7d-57a0-8ac9e64edf89\",\"__new\":true,\"__detached\":false,\"__removed\":false}"

        when:
        Parameter param = converter.parameterFromJson(json)

        then:
        param.name == 'name'
        param.alias == 'alias'
        param.mappedAlias == 'mappedAlias'
        param.orderNum == 1
        param.value.getClass() == EntityValue.class

        def value = param.value as EntityValue
        value.entityId == 'entityId'
        value.metaClassName == 'sec$User'
        value.viewName == '_local'
    }

    Parameter entityParameter() {
        EntityValue value = new EntityValue()
        value.entityId = 'entityId'
        value.metaClassName = 'sec$User'
        value.viewName = '_local'

        Parameter p = baseParameter()
        p.value = value
        return p
    }

    EntityValue someEntityValue() {
        EntityValue value = new EntityValue()
        value.entityId = 'entityId'
        value.metaClassName = 'sec$User'
        value.viewName = '_local'
        return value
    }

    Parameter enumStringParameter() {
        EnumValue value = new EnumValue()
        value.enumClassName = 'someEnumClass'
        value.enumValue = 'someValue'

        Parameter p = baseParameter()
        p.value = value
        return p
    }

    Parameter listEntitiesParameter() {
        EntityValue ent1 = new EntityValue()
        ent1.entityId = 'entity1'
        EntityValue ent2 = new EntityValue()
        ent2.entityId = 'entity2'

        ListEntitiesValue value = new ListEntitiesValue()
        value.entityValues = [ent1, ent2]

        Parameter p = baseParameter()
        p.value = value
        return p
    }

    Parameter baseParameter() {
        Parameter p = new Parameter()
        p.name = 'name'
        p.alias = 'alias'
        p.mappedAlias = 'mappedAlias'
        p.orderNum = 1
        return p
    }
}
