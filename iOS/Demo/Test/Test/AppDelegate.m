//
//  AppDelegate.m
//  Test
//
//  Created by zsl on 2020/8/3.
//  Copyright © 2020 FunPlus. All rights reserved.
//

#import "AppDelegate.h"
#import "TestViewController.h"
@interface AppDelegate ()

@end

@implementation AppDelegate


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    
    self.window = [[UIWindow alloc] initWithFrame:[UIScreen mainScreen].bounds];
    self.window.backgroundColor = [UIColor whiteColor];

    TestViewController * testViewController = [[TestViewController alloc] init];
    [self.window setRootViewController:testViewController];
    [self.window makeKeyAndVisible];

    return YES;

}
@end
