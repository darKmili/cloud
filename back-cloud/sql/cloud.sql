/*
 Navicat Premium Data Transfer

 Source Server         : mysql-localhost
 Source Server Type    : MySQL
 Source Server Version : 50717
 Source Host           : localhost:3306
 Source Schema         : test2

 Target Server Type    : MySQL
 Target Server Version : 50717
 File Encoding         : 65001

 Date: 23/05/2022 17:24:03
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for file
-- ----------------------------
DROP TABLE IF EXISTS `file`;
CREATE TABLE `file`  (
  `inode` bigint(255) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '文件唯一标识符',
  `filename` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '被加密文件名',
  `atime` timestamp(6) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '访问时间',
  `ctime` timestamp(6) NULL DEFAULT NULL COMMENT '创建时间',
  `message_digest` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件消息摘要',
  `mtime` timestamp(6) NULL DEFAULT NULL COMMENT '修改时间',
  `parent_inode` bigint(255) UNSIGNED NULL DEFAULT NULL COMMENT '父目录唯一标识符',
  `size` bigint(255) NULL DEFAULT NULL COMMENT '文件大小，单位bit',
  `state` enum('NEW','UPLOADING','UPLOADED','DESTROY') CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'NEW' COMMENT '文件状态',
  `type` enum('FILE','DIR') CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'FILE' COMMENT '文件类型',
  `user_id` bigint(20) UNSIGNED NULL DEFAULT NULL COMMENT '文件所属用户id',
  `block_size` int(11) NULL DEFAULT NULL COMMENT '文件块的数量',
  PRIMARY KEY (`inode`) USING BTREE,
  INDEX `file_ibfk_1`(`user_id`) USING BTREE,
  INDEX `file_ibfk_2`(`parent_inode`) USING BTREE,
  CONSTRAINT `file_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE SET NULL ON UPDATE SET NULL,
  CONSTRAINT `file_ibfk_2` FOREIGN KEY (`parent_inode`) REFERENCES `file` (`inode`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of file
-- ----------------------------
INSERT INTO `file` VALUES (0, 'root', '2022-05-23 17:20:13.295475', '2022-05-23 17:20:10.000000', NULL, NULL, NULL, NULL, 'UPLOADED', 'DIR', NULL, NULL);
INSERT INTO `file` VALUES (1, '测试文件夹1', '2022-05-23 17:20:17.322134', '2022-05-23 17:20:15.000000', NULL, NULL, 0, NULL, 'UPLOADED', 'DIR', 1, NULL);
INSERT INTO `file` VALUES (2, '测试文件夹2', '2022-05-23 17:20:21.270573', '2022-05-23 17:20:18.000000', NULL, NULL, 0, NULL, 'UPLOADED', 'DIR', 1, NULL);
INSERT INTO `file` VALUES (3, '测试文件1', NULL, NULL, NULL, NULL, 1, NULL, 'NEW', 'FILE', 1, NULL);
INSERT INTO `file` VALUES (4, '测试文件2', NULL, NULL, NULL, NULL, 2, NULL, 'NEW', 'FILE', 1, NULL);

-- ----------------------------
-- Table structure for file_block
-- ----------------------------
DROP TABLE IF EXISTS `file_block`;
CREATE TABLE `file_block`  (
  `block_inode` bigint(255) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '块唯一标识符',
  `fingerprint` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '块指纹，块哈希',
  `idx` int(11) NULL DEFAULT NULL COMMENT '第idx个块',
  `size` bigint(20) NULL DEFAULT NULL COMMENT '块大小,单位kb',
  `file_inode` bigint(255) UNSIGNED NULL DEFAULT NULL COMMENT '所属文件的唯一inode',
  `bucket` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '块存储桶',
  PRIMARY KEY (`block_inode`) USING BTREE,
  INDEX `file_block_ibfk_1`(`file_inode`) USING BTREE,
  CONSTRAINT `file_block_ibfk_1` FOREIGN KEY (`file_inode`) REFERENCES `file` (`inode`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of file_block
-- ----------------------------
INSERT INTO `file_block` VALUES (1, 'sadaddd', 1, 2345, 3, '1');
INSERT INTO `file_block` VALUES (2, 'dasda', 2, 234, 3, '1');
INSERT INTO `file_block` VALUES (3, 'dsdsaaaa', 1, 2345, 4, '2');
INSERT INTO `file_block` VALUES (4, '23sdasf', 2, 23, 4, '2');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `email` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户邮箱，主键',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `face` blob NULL COMMENT '用户头像',
  `client_random_value` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户随机码',
  `sha256verify_key` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '验证哈希',
  `encrypted_master_key` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '被加密的主密钥',
  `cur_load_time` datetime(6) NULL DEFAULT NULL COMMENT '当前登录时间',
  `last_load_time` datetime(6) NULL DEFAULT NULL COMMENT '上次登录时间',
  `register_time` datetime(6) NULL DEFAULT NULL COMMENT '用户注册时间',
  `used_capacity` bigint(20) NULL DEFAULT NULL COMMENT '用户存储容量',
  `total_capacity` bigint(20) NULL DEFAULT NULL COMMENT '系统为用户分配的总容量',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '1227642494@qq.com', 'leon', NULL, 'aaaaaa', 'aaaaaa', 'aaaaaa', NULL, NULL, NULL, 0, 60000);

SET FOREIGN_KEY_CHECKS = 1;
