//
//  LivehelpSupport+Interface.h
//  FPCustomerSDK
//
//  Created by zsl on 2021/7/30.
//  Copyright © 2021 FunPlus. All rights reserved.
//

#import "LivehelpSupportNavigationController.h"
#import "LivehelpSupport.h"

NS_ASSUME_NONNULL_BEGIN

//客服类型 ：机器人 人工
typedef enum : NSUInteger {
    BOT,
    HUMAN
} FPCustomerConversationType;

@interface LivehelpSupport (Interface)


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
+(LivehelpSupportNavigationController * _Nullable)showAllFAQs;

//会话类型 BOT-机器人(如果有客诉未解决将直接跳转到人工服务) HUMAN-人工
+(LivehelpSupportNavigationController * _Nullable)showConversationWithType:(FPCustomerConversationType)type;

//修改语言
+(void)setLanguage:(NSString*)language;

//获取用户未读消息
+ (void)unreadMessage:(void(^_Nullable)(BOOL issueExist,int unreadCount,BOOL isSuccess)) isUnread;

+ (NSString*)sdkVersion;

+ (void)errorLog:(void(^_Nonnull)(NSError * error))error;
@end

NS_ASSUME_NONNULL_END
