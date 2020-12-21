//
//  FPCustomerManager.h
//  FPCustomerService
//
//  Created by zsl on 2020/7/16.
//  Copyright © 2020 FunPlus. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN
@interface FPCustomerManager : NSObject

+(BOOL)fpCustomerInitWithAppid:(NSInteger)appId                     //必传
                        userId:(NSString * _Nonnull)userId          //必传
                        appKey:(NSString * _Nonnull)appKey          //必传
                  gameLanguage:(NSString * _Nonnull)gameLanguage    //必传
                        gameId:(NSString * _Nullable)gameId
                      userName:(NSString * _Nullable)userName
                      serverId:(NSString * _Nullable)serverId
                   networkType:(NSString * _Nullable)networkType
                          tags:(NSArray<NSString*> * _Nullable)tags
                      vipLevel:(NSInteger)vipLevel
                        custom:(NSDictionary * _Nullable)custom
                        domain:(NSString * _Nonnull)domain
               pushDeviceToken:(NSString * _Nullable)pushDeviceToken;

+ (instancetype)shareInstance;
@property(nonatomic,assign,readonly)BOOL initFinish;
@property(nonatomic,strong,readonly)NSDictionary * _Nullable messagesDictionary;
@property(nonatomic,strong,readonly)NSDictionary * _Nullable allDataDictionary;
@property(nonatomic,copy,readonly) NSString * _Nullable greeting;

@property(nonatomic,assign,readonly)NSInteger appId;
@property(nonatomic,copy,readonly)NSString * userId;
@property(nonatomic,copy,readonly)NSString * appKey;
@property(nonatomic,copy,readonly)NSString * gameLanguage;
@property(nonatomic,copy,readonly)NSString * gameId;
@property(nonatomic,copy,readonly)NSString * userName;
@property(nonatomic,assign,readonly)NSInteger  vipLevel;
@property(nonatomic,copy,readonly)NSString * networkType;
@property(nonatomic,strong,readonly)NSArray<NSString*> * tags;

@property(nonatomic,copy,readonly)NSString * serverId;
@property(nonatomic,strong,readonly)NSDictionary * custom;
@property(nonatomic,copy,readonly)NSString *domain;
@property(nonatomic,copy,readonly)NSString *pushDeviceToken;
@property(nonatomic,copy,readonly)NSString *sdkV;


+ (void)getUnreadStatus:(void(^_Nullable)(BOOL)) isUnread;
@end


NS_ASSUME_NONNULL_END
