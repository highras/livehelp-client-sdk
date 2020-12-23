# 智能客服Android sdk接入文档

### 版本支持
最低支持android4.4 （api19）

### 接入
1.在Android工程的AndroidManifest.xml，增加需要的配置：
	网络权限,如果已经有,则不需要添加该配置

2.将liveHelp-android.aar包拷贝到工程app中的libs文件夹下.

3.SDK初始化(须在app启动的时候调用):
	import com.ilivedata.customer;

	初始化的接口:
	CustomerData.getInstance().init(Context context,String _domain, int appId, String appKey, String userId, String userName, String gameLanguage, String gameId, String serverId, String networkType, int vipLevel, String[] tags, Map<String,String> customData,String _deviceToken)
    /**
     * @context    应用的applicationcontext（必传）
     * @param _domain  后台配置的项目域名（必传）
     * @param appId    项目id（必传）
     * @param appKey    项目key（必传）
     * @param userId    用户id（必传）
     * @param userName  用户名称
     * @param gameLanguage  控制台配置的语言(必传)
     * @param gameId    游戏应用商店ID
     * @param serverId  当前区服ID
     * @param networkType   网络类型
     * @param vipLevel  vip等级
     * @param tags      客诉标签(来自控制台标签)
     * @param customData 自定义参数
     * @param _deviceToken 推送token
     */

4.客服接口说明:(需要初始化完成后)
- 启用faq页面
	    CustomerData.getInstance().faqShow(Activity currentActivity);
- 启用robot页面
        CustomerData.getInstance().robotShow(Activity currentActivity);
- 拉取是否有未读
        getUnreadMsg(IUnreadCallback callback); //callback未读回调函数
