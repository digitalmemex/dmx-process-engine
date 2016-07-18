package com.digitalmemex.process.engine;

import de.deepamehta.core.osgi.PluginActivator;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.repository.ProcessDefinitionQuery;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.extension.osgi.el.OSGiExpressionManager;
import org.camunda.bpm.extension.osgi.engine.ProcessEngineFactory;
import org.h2.jdbcx.JdbcDataSource;
import org.osgi.framework.Bundle;

import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Path("/process")
@Produces("application/json")
public class ProcessEnginePlugin extends PluginActivator implements ProcessEngineService {

    public static final String BPM_EXTENSION_OSGI = "org.camunda.bpm.extension.osgi";

    private Logger logger = Logger.getLogger(getClass().getName());

    private ProcessEngine processEngine;

    public DataSource createDataSource() {
        logger.info("configure process engine data source");
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:dmx-process-db;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        dataSource.setPassword("");
        return dataSource;
    }

    @Override
    public void init() {
        logger.info("configure process engine");
        ProcessEngineFactory processEngineFactory = new ProcessEngineFactory();
        StandaloneProcessEngineConfiguration configuration = new StandaloneProcessEngineConfiguration();
        configuration.setDatabaseSchemaUpdate("true");
        configuration.setDataSource(createDataSource());
        configuration.setJobExecutorActivate(false);
        configuration.setExpressionManager(new OSGiExpressionManager());
        processEngineFactory.setProcessEngineConfiguration(configuration);

        logger.info("resolve engine factory bundle context");
        Bundle extension = null;
        for (Bundle bundle : getBundleContext().getBundles()) {
            if (BPM_EXTENSION_OSGI.equals(bundle.getSymbolicName())) {
                extension = bundle;
            }
        }
        if (extension == null) {
            throw new RuntimeException("Camunda BPM - Engine - OSGi bundle is not available");
        } else {
            processEngineFactory.setBundle(extension);
        }

        logger.info("init process engine");
        processEngineFactory.init();
        processEngine = processEngineFactory.getObject();


        logger.info("register process engine");
        getBundleContext().registerService(ProcessEngine.class.getName(), processEngine, null);
    }

    private ProcessInfo mapProcessInfo(ProcessDefinition definition) {
        ProcessInfo info = new ProcessInfo();
        info.setName(definition.getName());
        info.setDescription(definition.getDescription());
        info.setVersion(definition.getVersion());
        info.setKey(definition.getKey());
        info.setResource(definition.getResourceName());
        return info;
    }

    @Override
    @GET
    @Path("/engine")
    public EngineInfo getEngineName() {
        EngineInfo info = new EngineInfo();
        info.setName(processEngine.getName());
        return info;
    }

    @Override
    @GET
    @Path("/info/{name}")
    public ProcessInfo getProcessInfo(@PathParam("name") String name) {
        logger.info("get process definition " + name);
        ProcessDefinitionQuery query = processEngine.getRepositoryService().createProcessDefinitionQuery();
        ProcessDefinition definition = query.processDefinitionName(name).singleResult();
        return mapProcessInfo(definition);
    }


    @GET
    @Path("/clear")
    public List<String> clear() {
        logger.info("clear all deployments");
        List<String> deletedDeployments = new ArrayList<String>();
        List<Deployment> list = processEngine.getRepositoryService().createDeploymentQuery().list();
        for (Deployment d : list) {
            logger.info("delete deployment: " + d.getName());
            processEngine.getRepositoryService().deleteDeployment(d.getId());
            deletedDeployments.add(d.getName());
        }
        return deletedDeployments;
    }

    @Override
    @GET
    @Path("/list")
    public List<String> getProcessNameList() {
        logger.info("list process definitions");
        List<ProcessDefinition> list = processEngine.getRepositoryService().createProcessDefinitionQuery().list();
        List<String> names = new ArrayList<String>();
        for (ProcessDefinition pd : list) {
            names.add(pd.getName());
        }
        return names;
    }

    @Override
    @GET
    @Path("/start/{key}")
    public InstanceInfo startProcess(@PathParam("key") String key) {
        logger.info("start process " + key);
        ProcessInstance instance = processEngine.getRuntimeService().startProcessInstanceByKey(key);
        InstanceInfo info = new InstanceInfo();
        info.setKey(instance.getBusinessKey());
        info.setId(instance.getId());
        return info;
    }

    @Override
    public ProcessInfo deploy(String name, String fileName, InputStream deployStream) {
        logger.info("deploy " + name);
        Deployment deployment = processEngine.getRepositoryService().createDeployment()
                .name(name)
                .enableDuplicateFiltering(true)
                .addInputStream(fileName, deployStream)
                .deploy();
        ProcessDefinitionQuery query = processEngine.getRepositoryService().createProcessDefinitionQuery();
        ProcessDefinition definition = query.processDefinitionResourceName(fileName).singleResult();
        return mapProcessInfo(definition);
    }

}
