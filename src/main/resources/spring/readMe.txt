1.@Autowired与@Resource区别
	@Autowired默认按类型装配（这个注解是属业spring的），默认情况下必须要求依赖对象必须存在，如果要允许null 值，可以设置它的required属性为false，
	如：@Autowired(required=false) 
	如果我们想使用名称装配可以结合@Qualifier注解进行使用，如下：
	@Autowired() @Qualifier("baseDao")     
	private BaseDao baseDao;
	@Resource（这个注解属于J2EE的），默认安照名称进行装配，名称可以通过name属性进行指定，如果没有指定name属性，当注解写在字段上时，默认按照字段名查找；如果注解写在setter方法上默认
	按照属性名称装配，如果找不到按照类型装配，如果指定name就按name匹配，例如：
	@Resource(name="baseDao")     
	private BaseDao baseDao;
2.
	