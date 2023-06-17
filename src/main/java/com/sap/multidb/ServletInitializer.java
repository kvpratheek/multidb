package com.sap.multidb;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.annotation.WebListener;

import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.DispatcherServlet;

@WebListener
public class ServletInitializer implements ServletContextListener {

    protected ContextLoaderListener wrappedListener;

    public ServletInitializer() {
        final AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.registerShutdownHook();
        context.register(getContextConfiguration());
        wrappedListener = new ContextLoaderListener(context);
    }

    @Override
    public void contextDestroyed(final ServletContextEvent sce) {

    }

    @Override
    public void contextInitialized(final ServletContextEvent sce) {
        final ServletContext servletContext = sce.getServletContext();

        wrappedListener.contextInitialized(sce);
        final WebApplicationContext webAppCtx = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);

        Dynamic dynamic = servletContext.addServlet("dispatcher", new DispatcherServlet(webAppCtx));
        dynamic.addMapping("/employee");
        dynamic.addMapping("/onboard");
        dynamic.addMapping("/appschema");
        dynamic.setInitParameter("dispatchOptionsRequest", "true");
        dynamic.setLoadOnStartup(1);

        webAppCtx.getAutowireCapableBeanFactory().autowireBean(this);

    }

    protected Class<?> getContextConfiguration() {
//      return ContextConfiguration.class;
        return AppConfig.class;
    }

}
