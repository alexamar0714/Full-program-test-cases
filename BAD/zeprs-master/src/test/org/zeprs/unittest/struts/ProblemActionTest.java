package org.zeprs.unittest.struts;

import servletunit.struts.MockStrutsTestCase;

import java.io.File;

import org.cidrz.test.TestPrincipal;

/**
 * @author <a href="mailto:<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="74171f111818110d3406001d5a1b0613">[email protected]</a>">Chris Kelley</a>
 *         Date: Jun 22, 2005
 *         Time: 11:01:21 AM
 */
public class ProblemActionTest extends MockStrutsTestCase {

    // private ProblemForm bookEditForm;

    public ProblemActionTest(String testName) {
        super(testName);
    }

    public void setUp() throws Exception {
        super.setUp();
        // set the context directory to /zeprs
        // to find the /WEB-INF/web.xml
        setContextDirectory(new File("webapps/zeprs"));
    }

    public void testProblemAction() {

        setRequestPathInfo("/problem");
        addRequestParameter("patientId", "36");
        addRequestParameter("pregnancyId", "30");
        request.setRemoteAddr("127.0.0.1");
        TestPrincipal principal = new TestPrincipal();
        principal.setName("demo");
        request.setUserPrincipal(principal);
        String ipAddress = request.getRemoteAddr();
        actionPerform();
        //assertNotNull(((BookListForm)getActionForm()).getBooks());
        verifyForward("success");

    }
}


