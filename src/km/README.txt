beini-cache 模块使用须知


使用举例：
步骤一：在项目中添加beini-cache依赖(如下1所示)


步骤二：在service实现类中如下使用(建议在serviceImpl中集成缓存)

@Service("productService")
@Slf4j
@CacheConfig(cacheNames="product",cacheManager="",cacheResolver="",keyGenerator="")
public class ProductServiceImpl implements ProductService {
	@Cacheable(key="'product_'+#id")
	public ProductVo getProductById(Integer id) {
		return /*此处从数据库中获取对象数据*/;
	}
	@CacheEvict(key="'product_'+#id")
	public ProductVo updateProductById(Integer id) {
		return /*此处从数据库中获取对象数据*/;
	}
	@CachePut(key="'product_'+#id")
	public ProductVo updateProductById2(Integer id) {
		return /*此处从数据库中获取对象数据*/;
	}
}


1.添加POM依赖
	<dependency>
		<groupId>com.beini</groupId>
		<artifactId>beini-cache</artifactId>
		<version>${beini.cache.version}</version><!--目前版本为0.1-->
	</dependency>
	
2.在需要使用缓存的类上使用如下注解
	@CacheConfig(cacheNames="product")

3.在需要缓存数据的方法上使用如下注解
    /*key值为：product_id值*/
	@Cacheable(key="'product_'+#id")
	/*key值为：*/
	@Cacheable
	以下注解也是类似的意思
	
注意：
1.该beini-cache的依赖包含了如下依赖
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-data-redis</artifactId>
	</dependency>
	<dependency>
		<groupId>com.beini</groupId>
		<artifactId>beini-core</artifactId>
		<version>${beini.cache.version}</version><!--目前版本为0.1-->
	</dependency>

2.@CacheConfig、@Cacheable、@CachePut、@CachePut 皆为 org.springframework.cache.annotation 包下的注解类,其用法和spring原生支持的注解使用无异。


2.1 @CacheConfig(
	    cacheNames="cacheNames名称",
	    keyGenerator="keyGenerator名称",
	    cacheManager="cacheManager名称",
	    cacheResolver="cacheResolver名称")
	
	该注解中的cacheNames属性是为了给该类下的方法提供一个key索引,当然该注解可以省略该属性,那么该类中的其他缓存注解就必须包含该属性,否则编译不通过。
	如果定义了该属性,则会再缓存中增加了一个“cacheNames名称~keys”的ZSET数据类型。
	
	而keyGenerator、cacheManager、cacheResolver属性参数皆是继承了CachingConfigurerSupport类中的覆盖如下三个方法，并注册为Spring的Bean的调用作用。
具体方法覆盖写法可以依照如下beini-cache的配置类：com.beini.cache.config.RedisUtilConfig
	
	@Bean("keyGenerator名称")
	@Override
	public KeyGenerator keyGenerator() {
		/*key值生成策略,beini-cache提供的默认生成策略是：类名.方法名:参数值-*/
		return null;
	}
	@Bean("cacheManager名称")
	@Override
	public CacheManager cacheManager() {
		/*缓存管理方式*/
		return null;
	}
	@Bean("cacheResolver名称")
	@Override
	public CacheResolver cacheResolver() {
		/*缓存解析方式*/
		return null;
	}
	故，在需要特殊使用缓存的项目中可以单独覆盖重写这几个方法,并在此配置类上加上以下两个注解:@Configuration,@EnableCaching。

2.2 @Cacheable、@CachePut、@CachePut的注解属性都与原生spring的注解无异，可以参照如下了解详情：https://blog.csdn.net/sanjay_f/article/details/47372967

	@Cacheable 
	Spring 在执行 @Cacheable 标注的方法前先查看缓存中是否有数据，如果有数据，则直接返回缓存数据；若没有数据，执行该方法并将方法返回值放进缓存。 
	参数： value缓存名、 key缓存键值、 condition满足缓存条件、unless否决缓存条件 
	
	@CachePut 
	和 @Cacheable 类似，但会把方法的返回值放入缓存中, 主要用于数据新增和修改方法。
	
	@CacheEvict 
	方法执行成功后会从缓存中移除相应数据。 
	参数： value缓存名、 key缓存键值、 condition满足缓存条件、 unless否决缓存条件、 allEntries是否移除所有数据（设置为true时会移除所有缓存） 


2.2.1 如果@Cacheable、@CachePut、@CachePut中含有key属性(可以使用SEPL语言编辑),则会替代beini-cache的key生成策略。






