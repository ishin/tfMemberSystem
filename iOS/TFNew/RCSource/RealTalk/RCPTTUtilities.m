//
//  RCPTTUtilities.m
//  RongPTTKit
//
//  Created by Sin on 16/12/27.
//  Copyright © 2016年 RongCloud. All rights reserved.
//

#import "RCPTTUtilities.h"

@implementation RCPTTUtilities
+ (UIImage *)imageNamed:(NSString *)name ofBundle:(NSString *)bundleName {
  UIImage *image = nil;
  NSString *image_name = [NSString stringWithFormat:@"%@.png", name];
  NSString *resourcePath = [[NSBundle mainBundle] resourcePath];
  NSString *bundlePath =
  [resourcePath stringByAppendingPathComponent:bundleName];
  NSString *image_path = [bundlePath stringByAppendingPathComponent:image_name];
  image = [[UIImage alloc] initWithContentsOfFile:image_path];
  return image;
}
+ (UIImage *)imageNamedInPTTBundle:(NSString *)name {
  return [self imageNamed:name ofBundle:@"RongCloudPTT.bundle"];
}
@end
