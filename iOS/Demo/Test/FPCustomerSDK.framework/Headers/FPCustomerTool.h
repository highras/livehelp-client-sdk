


#import <UIKit/UIKit.h>

@interface FPCustomerTool : NSObject
+ (UIViewController *)getCurrentVC;

+ (NSString *)getUUIDInKeychain;

+ (NSString *)convertToJsonData:(NSDictionary *)dict;

+ (NSString *)convertToJsonDataNoSpace:(NSDictionary *)dict;

+ (NSString *)sha256HashFor:(NSString*)input;

+ (NSString *)getTime;

+ (NSString *)hmac:(NSString *)plaintext withKey:(NSString *)key;

+ (NSString *)getSystemLanguage;

+ (NSString *)getSystemVersion;

+ (NSString *)getIphoneType;

+ (NSString *)getCountryCode;

+ (NSString *)getIPAddress;

+ (NSString *)getCarrier;

+ (NSString *)getBundleId;

+ (NSString *)getAppname;

+ (NSString *)getVersion;

+ (NSString *)getBatteryState;

+ (NSString *)getCurrentBatteryLevel;

+(NSString *)getDiskTotalcapacity;
    
+(NSString *)getFreeDiskTotalcapacity;

+(NSDictionary *)paramerWithURL:(NSURL *)url;

+(NSArray*)sortASCII:(NSArray*)sourceArray;

+ (BOOL)isNotchScreen;
@end
