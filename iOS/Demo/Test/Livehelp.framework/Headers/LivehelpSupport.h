

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN


@interface LivehelpSupport : NSObject

@property(nonatomic,assign,readonly)NSInteger projectId;
@property(nonatomic,copy,readonly)NSString * userId;
@property(nonatomic,copy,readonly)NSString * projectKey;
@property(nonatomic,copy,readonly)NSString * userName;
@property(nonatomic,copy,readonly)NSString * domain;
@property(nonatomic,copy,readonly)NSString * pushDeviceToken;
@property(nonatomic,copy,readonly)NSArray * tags;
@property(nonatomic,copy,readonly)NSMutableDictionary * customData;


@end


NS_ASSUME_NONNULL_END
