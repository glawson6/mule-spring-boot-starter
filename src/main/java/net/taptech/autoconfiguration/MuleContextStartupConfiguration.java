package net.taptech.autoconfiguration;

import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.client.MuleClient;
import org.mule.api.context.MuleContextBuilder;
import org.mule.api.context.MuleContextFactory;
import org.mule.api.transaction.TransactionManagerFactory;
import org.mule.client.DefaultLocalMuleClient;
import org.mule.config.spring.SpringXmlConfigurationBuilder;
import org.mule.context.DefaultMuleContextBuilder;
import org.mule.context.DefaultMuleContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import javax.annotation.PostConstruct;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;


import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;

import org.mule.module.spring.transaction.SpringTransactionFactory;
import org.mule.module.spring.transaction.SpringTransactionManagerFactory;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

/**
 * Created by tap on 10/30/16.
 */
@Configuration
public class MuleContextStartupConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(MuleContextStartupConfiguration.class);

    private MuleContext muleContext;

    @Autowired
    private ResourceLoader resourceLoader = new DefaultResourceLoader();

    @Autowired
    private ApplicationContext context;

    @Value("${mule.config.files}")
    String muleConfigFiles;

    @Bean
    public UserTransaction userTransaction() throws Throwable {
        logger.info("Creating UserTransaction");
        UserTransactionImp userTransactionImp = new UserTransactionImp();
        userTransactionImp.setTransactionTimeout(1000);
        return userTransactionImp;
    }



    @Configuration
    @ConditionalOnClass(TransactionManager.class)
    public static class TransactionManagerConfig{
        @Bean(initMethod = "init", destroyMethod = "close")
        public TransactionManager transactionManager() throws Throwable {

            logger.info("Creating TransactionManager");
            UserTransactionManager userTransactionManager = new UserTransactionManager();
            userTransactionManager.setForceShutdown(false);
            return userTransactionManager;
        }
    }


    @Bean
    public PlatformTransactionManager platformTransactionManager(UserTransaction userTransaction, TransactionManager transactionManager)  throws Throwable {
        return new JtaTransactionManager(
                userTransaction,transactionManager);
    }

    @Bean
    public TransactionManagerFactory transactionManagerFactory(PlatformTransactionManager platformTransactionManager){
        SpringTransactionFactory factory = new SpringTransactionFactory();
        factory.setManager(platformTransactionManager);
        SpringTransactionManagerFactory managerFactory = new SpringTransactionManagerFactory();
        managerFactory.setTransactionManager(new JtaTransactionManager().getTransactionManager());
        return managerFactory;
    }

    @Bean
    MuleClient muleClient(MuleContext muleContext) throws MuleException {
        logger.info("Creating MuleClient");
        MuleClient client = new DefaultLocalMuleClient(muleContext);
        return client;

    }

    @Bean(name="muleContext")
    MuleContext muleContext() throws MuleException {
        logger.info("Creating MuleContext");
        MuleContextFactory muleContextFactory = new DefaultMuleContextFactory();
        logger.info("Loading Mule config files {}",muleConfigFiles);
        String [] configFiles = muleConfigFiles.split(",");
        SpringXmlConfigurationBuilder  builder = new SpringXmlConfigurationBuilder(configFiles);
        builder.setParentContext(context);
        MuleContextBuilder contextBuilder = new DefaultMuleContextBuilder();
        MuleContext context = muleContextFactory.createMuleContext(builder, contextBuilder);
        logger.info("Created MuleContext");
        return context;
    }

    @Configuration
    public static class MuleContextPostConstruct{
        @Autowired
        MuleContext muleContext;

        @PostConstruct
        MuleContext createMuleContext() throws MuleException {
            logger.info("Starting MuleContext....");
            muleContext.start();
            return muleContext;
        }
    }

}
