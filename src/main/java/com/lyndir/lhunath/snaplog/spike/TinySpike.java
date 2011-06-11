package com.lyndir.lhunath.snaplog.spike;

import com.lyndir.lhunath.opal.system.logging.Logger;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;


/**
 * <h2>{@link TinySpike}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>06 04, 2010</i> </p>
 *
 * @author lhunath
 */
public class TinySpike {

    static final Logger logger = Logger.get( TinySpike.class );

    @Test
    public void testWicket()
            throws Exception {

        WicketTester tester = new WicketTester( TestPage.class );
        tester.processRequestCycle();

        FormTester form = tester.newFormTester( "form" );
        form.submit();

        tester.assertNoErrorMessage();
    }

    public static class TestPage extends WebPage {

        @Override
        protected void onInitialize() {

            super.onInitialize();

            add( new Form<Void>( "form" ) {
                @Override
                protected void onInitialize() {

                    super.onInitialize();

                    add( new TextField<String>( "field", Model.<String>of("") ).setConvertEmptyInputStringToNull( false ).setRequired( true ) );
                }
            } );
        }
    }
}
