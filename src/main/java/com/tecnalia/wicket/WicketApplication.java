package com.tecnalia.wicket;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import net.ftlines.wicketsource.WicketSource;

import org.apache.log4j.Logger;
import org.apache.wicket.Application;
import org.apache.wicket.ResourceBundles;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.caching.FilenameWithVersionResourceCachingStrategy;
import org.apache.wicket.request.resource.caching.NoOpResourceCachingStrategy;
import org.apache.wicket.request.resource.caching.version.CachingResourceVersion;
import org.apache.wicket.serialize.java.DeflatedJavaSerializer;
import org.apache.wicket.settings.IRequestCycleSettings;
import org.apache.wicket.util.io.IOUtils;
import org.apache.wicket.util.string.Strings;
import org.openlca.eigen.NativeLibrary;
import org.wicketstuff.annotation.scan.AnnotatedMountScanner;

import com.google.javascript.jscomp.CompilationLevel;
import com.tecnalia.lca.app.Workspace;
import com.tecnalia.lca.app.util.Numbers;
import com.tecnalia.wicket.assets.base.ApplicationJavaScript;
import com.tecnalia.wicket.assets.base.FixBootstrapStylesCssResourceReference;

import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.markup.html.RenderJavaScriptToFooterHeaderResponseDecorator;
import de.agilecoders.wicket.core.markup.html.bootstrap.block.prettyprint.PrettifyCssResourceReference;
import de.agilecoders.wicket.core.markup.html.bootstrap.block.prettyprint.PrettifyJavaScriptReference;
import de.agilecoders.wicket.core.markup.html.references.ModernizrJavaScriptReference;
import de.agilecoders.wicket.core.request.resource.caching.version.Adler32ResourceVersion;
import de.agilecoders.wicket.core.settings.BootstrapSettings;
import de.agilecoders.wicket.core.settings.IBootstrapSettings;
import de.agilecoders.wicket.core.settings.ThemeProvider;
import de.agilecoders.wicket.extensions.javascript.GoogleClosureJavaScriptCompressor;
import de.agilecoders.wicket.extensions.javascript.YuiCssCompressor;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.html5player.Html5PlayerCssReference;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.html5player.Html5PlayerJavaScriptReference;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.OpenWebIconsCssReference;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.jqueryui.JQueryUICoreJavaScriptReference;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.jqueryui.JQueryUIDraggableJavaScriptReference;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.jqueryui.JQueryUIMouseJavaScriptReference;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.jqueryui.JQueryUIResizableJavaScriptReference;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.jqueryui.JQueryUIWidgetJavaScriptReference;
import de.agilecoders.wicket.extensions.request.StaticResourceRewriteMapper;
import de.agilecoders.wicket.less.BootstrapLess;
import de.agilecoders.wicket.themes.markup.html.bootswatch.BootswatchTheme;
import de.agilecoders.wicket.themes.markup.html.bootswatch.BootswatchThemeProvider;

/**
 * Application object for your web application.
 * If you want to run this application without deploying, run the Start class.
 * 
 * @see com.tecnalia.wicket.Start#main(String[])
 */
public class WicketApplication extends WebApplication
{
	
	// Get logger
	private static final Logger logger = Logger.getLogger(WicketApplication.class);
	// Properties
	private Properties properties;
	
    /**
     * Constructor.
     */
	public WicketApplication() {
		super();

		properties = loadProperties();
		setConfigurationType(RuntimeConfigurationType.valueOf(properties.getProperty("configuration.type")));
	}
	
	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends WebPage> getHomePage()
	{
		return com.tecnalia.wicket.pages.HomePage.class;
	}

	/**
	 * @see org.apache.wicket.Application#init()
	 */
	@Override
	public void init()
	{
		super.init();
		
		getApplicationSettings().setUploadProgressUpdatesEnabled(true);

        // deactivate ajax debug mode
        //getDebugSettings().setAjaxDebugModeEnabled(false);
		        
        // Bootstrap	
        configureBootstrap();
        //configureResourceBundles();
        
        // deactivate the performance optimization. This breaks the home page but enables ajax for prototype development
        //optimizeForWebPerformance();        
        
        new AnnotatedMountScanner().scanPackage("com.tecnalia.wicket.pages").mount(this);
        
        if (Strings.isTrue(properties.getProperty("cdn.useCdn"))) {
            final String cdn = properties.getProperty("cdn.baseUrl");

            StaticResourceRewriteMapper.withBaseUrl(cdn).install(this);
        }
        
        WicketSource.configure(this);
        
		// Test LCA application
		//AppLoader.load();
		// Load LCA application
		File workspace = Workspace.init();
		logger.debug("Workspace initialised at " + workspace);
		NativeLibrary.loadFromDir(workspace);
		logger.debug("olca-eigen loaded: " + NativeLibrary.isLoaded());
		Numbers.setDefaultAccuracy(5);
	}
	
    /**
     * Get Application for current thread.
     *
     * @return The current thread's Application
     */
    public static WicketApplication get() {
        return (WicketApplication) Application.get();
    }

    /**
     * loads all configuration properties from disk
     *
     * @return configuration properties
     */
    private Properties loadProperties() {
        Properties properties = new Properties();
        try {
            InputStream stream = getClass().getResourceAsStream("/config.properties");
            try {
                properties.load(stream);
            } finally {
                IOUtils.closeQuietly(stream);
            }
        } catch (IOException e) {
            throw new WicketRuntimeException(e);
        }
        return properties;
    }
    
    /**
     * configures wicket-bootstrap and installs the settings.
     */
    private void configureBootstrap() {
        final IBootstrapSettings settings = new BootstrapSettings();
        final ThemeProvider themeProvider = new BootswatchThemeProvider(BootswatchTheme.Readable);

        settings.setJsResourceFilterName("footer-container").setThemeProvider(themeProvider);       

        Bootstrap.install(this, settings);
        BootstrapLess.install(this);
    }
    
    /**
     * configure all resource bundles (css and js)
     */
    private void configureResourceBundles() {
        ResourceBundles bundles = getResourceBundles();
        bundles.addJavaScriptBundle(WicketApplication.class, "core.js",
                                    (JavaScriptResourceReference) getJavaScriptLibrarySettings().getJQueryReference(),
                                    (JavaScriptResourceReference) getJavaScriptLibrarySettings().getWicketEventReference(),
                                    (JavaScriptResourceReference) getJavaScriptLibrarySettings().getWicketAjaxReference(),
                                    ModernizrJavaScriptReference.instance()
        );

        bundles.addJavaScriptBundle(WicketApplication.class, "bootstrap.js",
                                    (JavaScriptResourceReference) Bootstrap.getSettings().getJsResourceReference(),
                                    (JavaScriptResourceReference) PrettifyJavaScriptReference.INSTANCE,
                                    ApplicationJavaScript.INSTANCE
        );

        getResourceBundles().addJavaScriptBundle(WicketApplication.class, "bootstrap-extensions.js",
                                                 JQueryUICoreJavaScriptReference.instance(),
                                                 JQueryUIWidgetJavaScriptReference.instance(),
                                                 JQueryUIMouseJavaScriptReference.instance(),
                                                 JQueryUIDraggableJavaScriptReference.instance(),
                                                 JQueryUIResizableJavaScriptReference.instance(),
                                                 Html5PlayerJavaScriptReference.instance()
        );

        bundles.addCssBundle(WicketApplication.class, "bootstrap-extensions.css",
                             Html5PlayerCssReference.instance(),
                             OpenWebIconsCssReference.instance()
        );

        bundles.addCssBundle(WicketApplication.class, "application.css",
                             (CssResourceReference) PrettifyCssResourceReference.INSTANCE,
                             FixBootstrapStylesCssResourceReference.INSTANCE
        );
    }
    
    /**
     * optimize wicket for a better web performance
     */
    private void optimizeForWebPerformance() {
        if (usesDeploymentConfig()) {
            getResourceSettings().setCachingStrategy(new FilenameWithVersionResourceCachingStrategy(
                    "-v-",
                    new CachingResourceVersion(new Adler32ResourceVersion())
            ));

            getResourceSettings().setJavaScriptCompressor(new GoogleClosureJavaScriptCompressor(CompilationLevel.SIMPLE_OPTIMIZATIONS));
            getResourceSettings().setCssCompressor(new YuiCssCompressor());

            getFrameworkSettings().setSerializer(new DeflatedJavaSerializer(getApplicationKey()));
        } else {
            getResourceSettings().setCachingStrategy(new NoOpResourceCachingStrategy());
        }

        setHeaderResponseDecorator(new RenderJavaScriptToFooterHeaderResponseDecorator());
        getRequestCycleSettings().setRenderStrategy(IRequestCycleSettings.RenderStrategy.ONE_PASS_RENDER);
    }
    
    /**
     * @return used configuration properties
     */
    public Properties getProperties() {
        return properties;
    }
}
