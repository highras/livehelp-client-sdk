//
//  LivehelpSupport.h
//  FPCustomerService
//
//  Created by zsl on 2020/7/16.
//  Copyright © 2020 FunPlus. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN


@interface LivehelpSupport : NSObject

@property(nonatomic,assign,readonly)NSInteger projectId;
@property(nonatomic,copy,readonly)NSString * userId;
@property(nonatomic,copy,readonly)NSString * projectKey;
@property(nonatomic,copy,readonly)NSString * userName;
@property(nonatomic,copy,readonly)NSString * domain;
@property(nonatomic,copy,readonly)NSString * pushDeviceToken;

@end


NS_ASSUME_NONNULL_END
