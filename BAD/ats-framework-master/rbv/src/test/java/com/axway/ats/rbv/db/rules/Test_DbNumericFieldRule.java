/*
 * Copyright 2017 Axway Software
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.axway.ats.rbv.db.rules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import com.axway.ats.rbv.BaseTest;
import com.axway.ats.rbv.db.DbMetaData;
import com.axway.ats.rbv.db.rules.DbNumericFieldRule;
import com.axway.ats.rbv.model.MetaDataIncorrectException;
import com.axway.ats.rbv.model.NoSuchMetaDataKeyException;
import com.axway.ats.rbv.model.RbvException;
import com.axway.ats.rbv.rules.AndRuleOperation;

public class Test_DbNumericFieldRule extends BaseTest {

    private static DbNumericFieldRule ruleTest1ExpectTrue  = new DbNumericFieldRule("table",
                                                                                    "column",
                                                                                    2,
                                                                                    "ruleTest1ExpectTrue",
                                                                                    true);
    private static DbNumericFieldRule ruleTest2ExpectTrue  = new DbNumericFieldRule("table",
                                                                                    "column2",
                                                                                    5,
                                                                                    "ruleTest2ExpectTrue",
                                                                                    true);

    private static DbNumericFieldRule ruleTest1ExpectFalse = new DbNumericFieldRule("table",
                                                                                    "column",
                                                                                    2,
                                                                                    "ruleTest1ExpectFalse",
                                                                                    false);
    private static DbNumericFieldRule ruleTest2ExpectFalse = new DbNumericFieldRule("table",
                                                                                    "column2",
                                                                                    5,
                                                                                    "ruleTest2ExpectFalse",
                                                                                    false);

    private static DbMetaData         testMetaData;

    @BeforeClass
    public static void setUpTest_DbDateFieldRule() {

        testMetaData = new DbMetaData();
    }

    @Test
    public void isMatchPositive() throws RbvException {

        testMetaData.putProperty("test.col", 3);

        DbNumericFieldRule rule = new DbNumericFieldRule("test", "col", 3, "isMatchPositive", true);
        assertTrue(rule.isMatch(testMetaData));
    }

    @Test
    public void isMatchNegative() throws RbvException {

        testMetaData.putProperty("test.col", 3);

        DbNumericFieldRule rule = new DbNumericFieldRule("test", "col", 2, "isMatchPositive", true);
        assertFalse(rule.isMatch(testMetaData));
    }

    @Test
    public void isMatchNullActualValuePositive() throws RbvException {

        testMetaData.putProperty("test.col", null);

        DbNumericFieldRule rule = new DbNumericFieldRule("test", "col", null, "isMatchPositive", true);
        assertTrue(rule.isMatch(testMetaData));
    }

    @Test
    public void isMatchNullActualValueNegative() throws RbvException {

        testMetaData.putProperty("test.col", null);

        DbNumericFieldRule rule = new DbNumericFieldRule("test", "col", 2, "isMatchPositive", true);
        assertFalse(rule.isMatch(testMetaData));
    }

    @Test
    public void isMatchNullExpectedValueNegative() throws RbvException {

        testMetaData.putProperty("test.col", 3);

        DbNumericFieldRule rule = new DbNumericFieldRule("test", "col", null, "isMatchPositive", true);
        assertFalse(rule.isMatch(testMetaData));
    }

    @Test
    public void isMatchExpectedTruePositive() throws RbvException {

        DbMetaData metaData = new DbMetaData();
        metaData.putProperty("table.column", 2);

        assertTrue(ruleTest1ExpectTrue.isMatch(metaData));
    }

    @Test
    public void isMatchExpectedTrueNegative() throws RbvException {

        DbMetaData metaData = new DbMetaData();
        metaData.putProperty("table.column", 22);

        assertFalse(ruleTest1ExpectTrue.isMatch(metaData));
    }

    @Test
    public void isMatchExpectedFalsePositive() throws RbvException {

        DbMetaData metaData = new DbMetaData();
        metaData.putProperty("table.column", 22);

        assertTrue(ruleTest1ExpectFalse.isMatch(metaData));
    }

    @Test
    public void isMatchExpectedFalseNegative() throws RbvException {

        DbMetaData metaData = new DbMetaData();
        metaData.putProperty("table.column", 2);

        assertFalse(ruleTest1ExpectFalse.isMatch(metaData));
    }

    @Test
    public void isMatchExpectedTrueMultipleRulesPositive() throws RbvException {

        DbMetaData metaData = new DbMetaData();
        metaData.putProperty("table.column", 2);
        metaData.putProperty("table.column2", 5);

        AndRuleOperation andRule = new AndRuleOperation();
        andRule.addRule(ruleTest1ExpectTrue);
        andRule.addRule(ruleTest2ExpectTrue);

        assertTrue(andRule.isMatch(metaData));
    }

    @Test( expected = NoSuchMetaDataKeyException.class)
    public void isMatchExpectedTrueMultipleRulesNegativeNoMetaData() throws RbvException {

        DbMetaData metaData = new DbMetaData();
        metaData.putProperty("test1", 3);

        AndRuleOperation andRule = new AndRuleOperation();
        andRule.addRule(ruleTest1ExpectTrue);
        andRule.addRule(ruleTest2ExpectTrue);

        assertFalse(andRule.isMatch(metaData));
    }

    @Test
    public void isMatchExpectedTrueMultipleRulesNegativeDontMatch() throws RbvException {

        DbMetaData metaData = new DbMetaData();
        metaData.putProperty("table.column", 2);
        metaData.putProperty("table.column2", 6);

        AndRuleOperation andRule = new AndRuleOperation();
        andRule.addRule(ruleTest1ExpectTrue);
        andRule.addRule(ruleTest2ExpectTrue);

        assertFalse(andRule.isMatch(metaData));
    }

    @Test( expected = NoSuchMetaDataKeyException.class)
    public void isMatchExpectedFalseMultipleRulesPositiveNoMetaData() throws RbvException {

        DbMetaData metaData = new DbMetaData();
        metaData.putProperty("table.column", 3);

        AndRuleOperation andRule = new AndRuleOperation();
        andRule.addRule(ruleTest1ExpectFalse);
        andRule.addRule(ruleTest2ExpectFalse);

        assertTrue(andRule.isMatch(metaData));
    }

    @Test
    public void isMatchExpectedFalseMultipleRulesPositiveDontMatch() throws RbvException {

        DbMetaData metaData = new DbMetaData();
        metaData.putProperty("table.column", 2);
        metaData.putProperty("table.column2", 18);

        AndRuleOperation andRule = new AndRuleOperation();
        andRule.addRule(ruleTest1ExpectFalse);
        andRule.addRule(ruleTest2ExpectFalse);

        assertFalse(andRule.isMatch(metaData));
    }

    @Test
    public void isMatchExpectedFalseMultipleRulesNegative() throws RbvException {

        DbMetaData metaData = new DbMetaData();
        metaData.putProperty("table.column", 2);
        metaData.putProperty("table.column2", 5);

        AndRuleOperation andRule = new AndRuleOperation();
        andRule.addRule(ruleTest1ExpectFalse);
        andRule.addRule(ruleTest2ExpectFalse);

        assertFalse(andRule.isMatch(metaData));
    }

    @Test
    public void isMatchExpectedTrueWrongNumericType() throws RbvException {

        DbMetaData metaData = new DbMetaData();
        metaData.putProperty("table.column", (long) 2);

        assertFalse(ruleTest1ExpectTrue.isMatch(metaData));
    }

    @Test( expected = MetaDataIncorrectException.class)
    public void isMatchIncorrectMetaData() throws RbvException {

        DbMetaData metaData = new DbMetaData();
        metaData.putProperty("table.column", "asdasd");

        ruleTest1ExpectFalse.isMatch(metaData);
    }
}
