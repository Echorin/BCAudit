package com.oschain.fastchaindb.common.config;



//@Aspect
//@Configuration
//@PropertySource(value = "classpath:appxchain.properties")

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource(locations= {"classpath:spring/applicationContext.xml"})
public class AppConfig {

//    @Value("${apppath}")
//    private String apppath;
//
//  @Bean
//   public String txAdvice() {
//      //System.out.println("aaaa------"+apppath);
//      return "a";
//    }

//    private static final String AOP_POINTCUT_EXPRESSION = "execution(* com.***.service..*.*(..))";
//
//    @Autowired
//    private PlatformTransactionManager transactionManager;
//
//    @Bean
//    public TransactionInterceptor txAdvice() {
//
//        DefaultTransactionAttribute txAttr_REQUIRED = new DefaultTransactionAttribute();
//        txAttr_REQUIRED.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
//
//        DefaultTransactionAttribute txAttr_REQUIRED_READONLY = new DefaultTransactionAttribute();
//        txAttr_REQUIRED_READONLY.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
//        txAttr_REQUIRED_READONLY.setReadOnly(true);
//
//        NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();
//
//        source.addTransactionalMethod("save*", txAttr_REQUIRED);
//        source.addTransactionalMethod("delete*", txAttr_REQUIRED);
//        source.addTransactionalMethod("update*", txAttr_REQUIRED);
//        source.addTransactionalMethod("exec*", txAttr_REQUIRED);
//        source.addTransactionalMethod("set*", txAttr_REQUIRED);
//
//        source.addTransactionalMethod("get*", txAttr_REQUIRED_READONLY);
//        source.addTransactionalMethod("query*", txAttr_REQUIRED_READONLY);
//        source.addTransactionalMethod("find*", txAttr_REQUIRED_READONLY);
//        source.addTransactionalMethod("list*", txAttr_REQUIRED_READONLY);
//        source.addTransactionalMethod("count*", txAttr_REQUIRED_READONLY);
//        source.addTransactionalMethod("is*", txAttr_REQUIRED_READONLY);
//
//        return new TransactionInterceptor(transactionManager, source);
//    }
//
//    @Bean
//    public Advisor txAdviceAdvisor() {
//        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
//        pointcut.setExpression(AOP_POINTCUT_EXPRESSION);
//        return new DefaultPointcutAdvisor(pointcut, txAdvice());
//    }

}