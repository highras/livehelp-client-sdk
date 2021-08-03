SDK 接入说明
=
系统要求
-

   *  支持系统iOS10以上

SDK支持
-
   *  采用Objective-C语言开发
   *  包含armv7,armv7s,arm64,i386,x86_64指令集, 可运行真机 + 模拟器 

集成依赖
-

   *  拖入FPCustomerSDK.framework 引入头文件 #import <FPCustomerSDK/FPCustomerSDK.h>
   
   *  拖入 FPCustomerSDK_Image 导入 Assets.xcassets
    
   *  info.plist 
   
           添加  Privacy - Photo Library Usage Description  相册权限       
           添加  Privacy - Camera Usage Description  相机权限
         
    
接口
-

   /// 初始化
/// @param appId 项目id(必传)
/// @param secretKey 项目key(必传)
/// @param domain 后台配置的项目域名(必传)
/// @param language 游戏语言(必传)
+(BOOL)initWithAppId:(NSInteger)appId
           secretKey:(NSString * _Nonnull)secretKey
              domain:(NSString * _Nonnull)domain
            language:(NSString * _Nonnull)language;


/// 设定用户属性 (必须调用(需要在init之后调用) 否则无法调用其它接口)
/// @param userId 用户id（必传）
/// @param userName 用户名称
/// @param avatar 用户头像url
/// @param language 用户语言
/// @param email 用户邮箱
/// @param tags 用户标签     用户身上的标签，用于分类，自动化过滤等
/// @param customData 自定义K/V信息，将显示在用户客诉详情信息中，辅助客服解决问题
/// @param deviceToken 推送token(可以再控制台设置推送)
/// @param resetResult 设置结果
+(void)resetUserInfoWithUserId:(NSString * _Nonnull)userId
                      userName:(NSString * _Nullable)userName
                        avatar:(NSString * _Nullable)avatar
                      language:(NSString * _Nonnull)language
                         email:(NSString * _Nullable)email
                          tags:(NSArray<NSString*> * _Nullable)tags
                    customData:(NSDictionary * _Nullable)customData
                   deviceToken:(NSString * _Nullable)deviceToken
                   resetResult:(void(^)(BOOL isSuccess))resetResult;

//常见问题列表
+(FPNavigationController * _Nullable)showAllFAQs;

//会话类型 BOT-机器人(如果有客诉未解决将直接跳转到人工服务) HUMAN-人工
+(FPNavigationController * _Nullable)showConversationWithType:(FPCustomerConversationType)type;

//修改语言
+(void)setLanguage:(NSString*)language;

//获取用户未读消息
+ (void)unreadMessage:(void(^_Nullable)(BOOL issueExist,int unreadCount,BOOL isSuccess)) isUnread;
