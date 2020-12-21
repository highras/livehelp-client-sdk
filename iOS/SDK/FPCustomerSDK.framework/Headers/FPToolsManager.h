//
//  FPToolsManager.h
//  FPCustomerService
//
//  Created by zsl on 2020/7/6.
//  Copyright © 2020 FunPlus. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "FPCustomerTool.h"
#import <UIKit/UIKit.h>
NS_ASSUME_NONNULL_BEGIN


#define FPCustomerService_Color16Hex(rgbValue) [UIColor colorWithRed:((float)((rgbValue & 0xFF0000) >> 16))/255.0 green:((float)((rgbValue & 0xFF00) >> 8))/255.0 blue:((float)(rgbValue & 0xFF))/255.0 alpha:1.0]
#define FPStatusBarH [[UIApplication sharedApplication] statusBarFrame].size.height
#define FPNavigationH FPStatusBarH + self.navigationController.navigationBar.frame.size.height

//#define FPIsIPhoneX (CGSizeEqualToSize(CGSizeMake(375.f, 812.f), [UIScreen mainScreen].bounds.size) || CGSizeEqualToSize(CGSizeMake(812.f, 375.f), [UIScreen mainScreen].bounds.size)) || (CGSizeEqualToSize(CGSizeMake(414.f, 896.f), [UIScreen mainScreen].bounds.size) || CGSizeEqualToSize(CGSizeMake(896.f, 414.f), [UIScreen mainScreen].bounds.size))

#define FPIsIPhoneX  [FPCustomerTool isNotchScreen]

#define FPNavHeight (FPIsIPhoneX ? 88.f : 64.f)
#define FPSafeTopMargin (FPIsIPhoneX ? 24.f : 0.f)
#define FPSafeBottomMargin (FPIsIPhoneX ? 34.f : 0.f)

@interface FPToolsManager : NSObject
typedef void (^SuccessCall)(NSDictionary * responseDic,NSString * statusCode);
typedef void (^FailureCall)(NSError *error);
+ (void)postRequest:(NSString *)url
               path:(NSString *)path
          parameter:(NSString*)parameter
         parameters:(nullable NSDictionary *)parametersDic
            success:(SuccessCall)success
            failure:(FailureCall)failure;
+ (void)getRequest:(NSString *)url
              path:(NSString *)path
         parameter:(NSString*)parameter
           success:(SuccessCall)success
           failure:(FailureCall)failure;
+ (UIImage *)imageWithColor:(UIColor *)color;
+ (NSString *)timestampSwitchTime:(NSInteger)timestamp;
+ (CGFloat)getHeightWithTextString:(NSString *)textStr font:(UIFont*)font width:(CGFloat)width;
+(void)saveFaqId:(NSString*)faqId isHelp:(BOOL)isHelp;
+(int)getHelpResult:(NSString*)faqId;
@end

NS_ASSUME_NONNULL_END
