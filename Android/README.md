### 版本支持
- 最低支持Android版本为5.0(api21)

### 依赖集成
 dependency in Gradle
    allprojects {
            repositories {
                maventral()
            }
        }
    dependencies {
        implementation 'com.github.highras:livehelp-android:1.0.0'
    }
### 使用说明
- RTM需要的权限
  ~~~
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.INTERNET"/>
    ~~~

###接口说明
~~~
 /**
     * @context             应用的appliaction(必传)
     * @param domain        后台配置的项目域名(必传)
     * @param projectId     项目id(必传)
     * @param projectKey    项目key(必传)
     * @param lang          游戏语言(必传)
     */
    public boolean init(Context applicationContext, int projectId, String projectKey, String domain, String lang) ;

    /**
     * 设置游戏语言
     * @param lang
     */
    public void setLanguage(String lang);


    /** 设定用户属性 (必须调用(需要在init之后调用) 否则无法调用其它接口)
     * @param userId    用户id（必传）
     * @param userName  用户名称
     * @param avatar    用户头像url
     * @param email     用户邮箱
     * @param tags      用户标签 	String 	optional 	用户身上的标签，用于分类，自动化过滤等
     * @param email     电子邮箱
     * @param customData 自定义K/V信息，将显示在用户客诉详情信息中，辅助客服解决问题
     * @param deviceToken 推送token(可以再控制台设置推送)
     */
    public void setUserInfo(String userId, String userName, String avatar, String email, List<String> tags,
                            Map<String, String> customData, String deviceToken, UserInterface.IUserCallback callback );

    /**
     * 用户下线(退出后 如果想再次进入客服系统 需再次调用setUserInfo才能正常进入智能客服)
     */
    public void resetUserInfo();


    /**
     * 获取用户未读消息
     */
    public void unreadMessage(UserInterface.IUnreadCallback callback);

    /**
     *
     * @param type 会话类型 BOT-机器人(如果有客诉未解决将直接跳转到人工服务) HUMAN-人工
     */
    public void showConversation( Activity activity,UserInterface.ConversationType type);

    /**
     * 打开常见问题列表(需要在后台项目自己配置问题列表)
     * @param activity
     */
    public void showAllFAQs(Activity activity);
~~~

### 使用示例
 ~~~
    CustomerService service = new CustomerService();

    service.init(getApplicationContext(),projectid, projectkeys, projectdomain, lang);

    service.setUserInfo(...)

    service.showConversation(MainActivity.this, UserInterface.ConversationType.BOT);

~~~
