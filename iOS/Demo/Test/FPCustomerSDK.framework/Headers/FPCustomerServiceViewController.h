

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface FPCustomerServiceViewController : UIViewController

+ (instancetype)initWithAppId:(NSInteger)appId          //唯一标识,从客服后台获取 必传
                       appKey:(NSString *)appKey        //唯一密钥,从客服后台获取 必传
                       userId:(NSString *)userId        //设置玩家id
                     userName:(NSString *)userName      //设置玩家名称
                       gameId:(NSString *)gameId        //设置游戏id
                     gameLang:(NSString *)gameLang      //设置游戏语言
                     vipLevel:(NSInteger)vipLevel;      //设置vip等级
@end

NS_ASSUME_NONNULL_END
