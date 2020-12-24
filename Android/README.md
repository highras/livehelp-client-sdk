# 智能客服Android sdk接入文档

### 版本支持
最低支持android4.4 （api19）

### 接入 
1.在Android工程的AndroidManifest.xml，增加需要的配置：<br/>
* 网络权限,如果已经有,则不需要添加该配置
    `<uses-permission android:name="android.permission.INTERNET" />`<br/>
* sdk内部ui使用约束布局 如果没有请在build.gradle中引用<br/>
    dependencies {
        implementation`'androidx.constraintlayout:constraintlayout:1.1.3'`
  

2.将liveHelp-android.aar包拷贝到工程app中的libs文件夹下.

3.SDK初始化(须在app启动的时候调用):
	import com.ilivedata.customer.*;

	初始化的接口:
	CustomerData.getInstance().init(Context context,String _domain, int projectId, String projectKey, String userId, String userName, String gameLanguage, String gameId, String serverId, String networkType, int vipLevel, String[] tags, Map<String,String> customData,String _deviceToken)
    /**
     * @context    应用的applicationcontext（必传）
     * @param _domain  后台配置的项目域名（必传）
     * @param projectId    项目id（必传）
     * @param projectKey    项目key（必传）
     * @param userId    用户id（必传）
     * @param userName  用户名称
     * @param gameLanguage 游戏语言(必传) [支持语言](https://docs.ilivedata.com/alt/language/)
     * @param gameId    游戏应用商店ID
     * @param serverId  当前区服ID
     * @param networkType   网络类型
     * @param vipLevel  vip等级
     * @param tags      标签名列表(大小写敏感), 标签从客服控制台创建,设置->标签设置
     * @param customData 自定义参数,传入信息会显示在控制台的客诉详情中
     * @param _deviceToken 推送token(可以再控制台设置推送)
     */

4.客服接口说明:(需要初始化完成后)
- 启用faq页面
	    void CustomerData.getInstance().faqShow(Activity currentActivity);
- 启用robot页面
        void CustomerData.getInstance().robotShow(Activity currentActivity);
- 拉取是否有未读
        void getUnreadMsg(IUnreadCallback callback); <br/>callback 未读回调接口
    ~~~
    public interface IUnreadCallback {
        void getMsg(boolean msg);
    }
    ~~~
