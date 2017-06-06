-- phpMyAdmin SQL Dump
-- version 4.5.4.1deb2ubuntu2
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: 2016-12-27 23:24:48
-- 服务器版本： 5.7.16-0ubuntu0.16.04.1
-- PHP Version: 7.0.8-0ubuntu0.16.04.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `tfdb`
--
CREATE DATABASE IF NOT EXISTS `organ` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `organ`;

-- --------------------------------------------------------

--
-- 表的结构 `t_organ`：组织
--

CREATE TABLE `t_organ` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(6) comment '企业码',
  `name` VARCHAR(256) comment '企业名称',
  `shortname` VARCHAR(256) comment '企业简称',
  `englishname` VARCHAR(256) comment '企业英文名称',
  `logo` VARCHAR(256) comment '企业log',
  `domain` VARCHAR(256) comment '企业域名',
  `province_id` INT DEFAULT 0 comment '省份id',
  `city_id` INT DEFAULT 0 comment '城市id',
  `district_id` INT DEFAULT 0 comment '地区id',
  `postcode` VARCHAR(20) comment '邮编',
  `contact` VARCHAR(256) comment '联系人',
  `address` VARCHAR(1024) comment '地址',
  `telephone` VARCHAR(50) comment '电话',
  `fax` VARCHAR(50) comment '传真',
  `email` VARCHAR(256) comment 'email',
  `website` VARCHAR(256) comment '网址',
  `inward_id` INT NULL DEFAULT 0 COMMENT '企业性质',
  `industry_id` INT NULL DEFAULT 0 COMMENT '主营行业',
  `subdustry_id` INT NULL DEFAULT 0 COMMENT '主营行业',
  `capital` INT NULL DEFAULT 0 COMMENT '注册资金',
  `membernumber` INT NULL DEFAULT 0 COMMENT '成员个数',
  `computernumber` INT  NULL DEFAULT 0 COMMENT '计算机台数',
  `ad` VARCHAR(1024) COMMENT '广告语',
  `intro` VARCHAR(1024) comment '介绍',
  `listorder` INT DEFAULT 0 comment '用来排序',
   PRIMARY KEY(id)
) ENGINE=InnoDB;

alter table `t_organ` AUTO_INCREMENT=1;

-- --------------------------------------------------------

--
-- 表的结构 `t_branch`：部门
--

CREATE TABLE `t_branch` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `organ_id` INT NOT NULL DEFAULT 0 comment '企业id',
  `parent_id` INT NOT NULL DEFAULT 0 comment '父id',
  `name` VARCHAR(256) comment '部门名称',
  `manager_id` INT NOT NULL DEFAULT 0 COMMENT '部门经理',
  `address` VARCHAR(1024) comment '地址',
  `website` VARCHAR(256) comment '网址',
  `telephone` VARCHAR(50) comment '电话',
  `fax` VARCHAR(50) comment '传真',
  `intro` VARCHAR(1024) comment '介绍',
  `listorder` INT NULL DEFAULT 0 comment '用来排序',
  `isdel` char(1) default '1' comment '逻辑删除,0已删除，1未删除',
  `nogroup` char(1) default '0' comment '是否为未分组,1是0否',
   PRIMARY KEY(id)
) ENGINE=InnoDB;

alter table `t_branch` AUTO_INCREMENT=101;
-- --------------------------------------------------------

--
-- 表的结构 `t_member`：成员
--

CREATE TABLE `t_member` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `account` VARCHAR(256) comment '账号',
  `password` VARCHAR(32) comment '密码',
  `token` VARCHAR(256) COMMENT '验证token',
  `createtokendate` INT DEFAULT 0 COMMENT '创建token时间到秒',
  `fullname` VARCHAR(256) comment '名字',
  `pinyin` VARCHAR(256) COMMENT '姓名首字母',
  `allpinyin` VARCHAR(256) COMMENT '姓名全拼',
  `workno` VARCHAR(50) COMMENT '工号',
  `sex` CHAR(1) not null default '1' comment '性别1男2女',
  `birthday` VARCHAR(8) comment '生日', 
  `logo` VARCHAR(256) comment '头像logo',
  `email` VARCHAR(256) comment 'email',
  `mobile` VARCHAR(50) comment '手机',
  `telephone` VARCHAR(50) comment '电话',
  `address` VARCHAR(1024) comment '地址',
  `organ_id` INT DEFAULT 0 comment '公司id',
  `groupmax` INT DEFAULT 0 COMMENT '可建群数量',
  `groupuse` INT DEFAULT 0 COMMENT '已建群数量',
  `intro` VARCHAR(1024) comment '介绍',
  `superadmin` int(1) comment '是否可管理独立后台',
  `isdel` int(1) default 1 comment '是否为未分组,1是0否',
   PRIMARY KEY(id)
) ENGINE=InnoDB;

alter table `t_member` AUTO_INCREMENT=10001;

-- --------------------------------------------------------

--
-- 表的结构 `t_branch_member`部门-成员关系
--

CREATE TABLE `t_branch_member` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `branch_id` INT NOT NULL DEFAULT 0 comment '部门id',
  `member_id` INT NOT NULL DEFAULT 0 comment '成员id',
  `position_id` INT NOT NULL DEFAULT 0 comment '职位id',
  `is_master` CHAR(1) DEFAULT '0' COMMENT '0非主要职能，1主要智能',
  `isdel` char(1) default '1' comment '0已删除,1未删除',
  `listorder` INT NULL DEFAULT 0,
   PRIMARY KEY(id)
) ENGINE=InnoDB;

-- --------------------------------------------------------

--
-- 表的结构 `t_role`：角色
--

CREATE TABLE `t_role` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(256) comment '角色名称',
  `organid` int not null default 0 comment '公司id',	
  `rolelevel` char(1) not null default 0 comment '角色等级',
  `listorder` INT NOT NULL DEFAULT 0 comment '',
   PRIMARY KEY(id)
) ENGINE=InnoDB;

-- --------------------------------------------------------

--
-- 表的结构 `t_priv`：权限
--

CREATE TABLE `t_priv` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `parent_id` INT NOT NULL DEFAULT 0 comment '父权限id',
  `name` VARCHAR(256) comment '权限名称',
  `category` CHAR(1) DEFAULT '0' COMMENT '种类：1权限，2层级限制',
  `grouping` CHAR(1) DEFAULT '0' COMMENT '0非分组记录，1分组记录',
  `url` VARCHAR(512) NOT NULL COMMENT '按url控制权限',
  `app` varchar(50) NOT NULL comment '所属应用',
  `organid` int(11) NOT NULL default 0 comment '公司id',
  `listorder` INT DEFAULT 0,
   PRIMARY KEY(id)
) ENGINE=InnoDB;

-- --------------------------------------------------------

--
-- 表的结构 `t_role_priv`：角色-权限关系
--

CREATE TABLE `t_role_priv` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `role_id` INT NOT NULL DEFAULT 0 comment '角色id',
  `priv_id` INT NOT NULL DEFAULT 0 comment '权限id',
   PRIMARY KEY(id)
) ENGINE=InnoDB;

-- --------------------------------------------------------

--
-- 表的结构 `t_member_role`：成员-角色关系
--

CREATE TABLE `t_member_role` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `member_id` INT NOT NULL DEFAULT 0 comment '成员id',
  `role_id` INT NOT NULL DEFAULT 0 comment '角色id',
  `isdel` char(1) default '1' comment '是否为未分组,1是0否',
  `listorder` INT DEFAULT 0,
   PRIMARY KEY(id)
) ENGINE=InnoDB;

-- --------------------------------------------------------

--
-- 表的结构 `t_position`：职务
--

CREATE TABLE `t_position` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `organ_id` INT NOT NULL DEFAULT 0 comment '公司id',
  `name` VARCHAR(50) comment '公司名称',
  `listorder` INT NOT NULL DEFAULT 0,
   PRIMARY KEY(id)
) ENGINE=InnoDB;

-- --------------------------------------------------------

--
-- 表的结构 `t_province`：字典：省份
--

CREATE TABLE `t_province` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(256) comment '省份名',
  `listorder` INT DEFAULT 0,
   PRIMARY KEY(id)
) ENGINE=InnoDB;

-- --------------------------------------------------------

--
-- 表的结构 `t_city`：字典：城市
--

CREATE TABLE `t_city` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `province_id` INT NOT NULL DEFAULT 0,
  `name` VARCHAR(256) comment '城市名',
  `listorder` INT NULL DEFAULT 0,
   PRIMARY KEY(id)
) ENGINE=InnoDB;

-- --------------------------------------------------------

--
-- 表的结构 `t_district`：字典：地区
--

CREATE TABLE `t_district` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `city_id` INT NOT NULL DEFAULT 0,
  `name` VARCHAR(256) comment '地区名',
  `listorder` INT NULL DEFAULT 0,
   PRIMARY KEY(id)
) ENGINE=InnoDB;

-- --------------------------------------------------------

--
-- 表的结构 `t_inward`：字典：企业性质
--

CREATE TABLE `t_inward` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(256) comment '企业性质名',
  `listorder` INT NULL DEFAULT 0,
   PRIMARY KEY(id)
) ENGINE=InnoDB;

-- --------------------------------------------------------

--
-- 表的结构 `t_industry`：字典：主行业
--

CREATE TABLE `t_industry` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(256) comment '主行业名',
  `listorder` INT NULL DEFAULT 0,
   PRIMARY KEY(id)
) ENGINE=InnoDB;

-- --------------------------------------------------------

--
-- 表的结构 `t_subdustry`：字典：子行业
--

CREATE TABLE `t_subdustry` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `industry_id` INT NOT NULL DEFAULT 0,
  `name` VARCHAR(256) comment '子行业名',
  `listorder` INT NOT NULL DEFAULT 0,
   PRIMARY KEY(id)
) ENGINE=InnoDB;

-- --------------------------------------------------------

--
-- 表的结构 `t_sex`：字典：性别
--

CREATE TABLE `t_sex` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(256) comment '性别名',
  `listorder` INT NULL DEFAULT 0,
   PRIMARY KEY(id)
) ENGINE=InnoDB;

-- --------------------------------------------------------

--
-- 表的结构 `t_textcode`：手机验证码保存
--
CREATE TABLE `t_textcode` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `phone_number` VARCHAR(256) NOT NULL DEFAULT '0' comment '电话号码',
  `text_code` VARCHAR(256) NOT NULL DEFAULT '0' comment '验证码',
  `create_time` BIGINT(11) NOT NULL DEFAULT '0' comment '创建时间',
   PRIMARY KEY(id)
) ENGINE=InnoDB;

--
-- 表的结构 `t_cutlogtemp`：头像裁剪临时库
--
CREATE TABLE `t_cutlogtemp` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL DEFAULT '0' comment '成员id',
  `log_name` VARCHAR(256) NOT NULL DEFAULT '0' comment '头像',
  `isDel` char(1) default '1' comment '是否为未分组,1是0否',
   PRIMARY KEY(id)
) ENGINE=InnoDB;


--应用数据表
CREATE TABLE `t_appsecret` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `appname` VARCHAR(50) NOT NULL DEFAULT '0' comment '应用名',
  `appId` VARCHAR(100) NOT NULL DEFAULT '0' comment '应用appid',
  `secert` VARCHAR(200) NOT NULL DEFAULT '0' comment '应用secert',
  `callbackurl` VARCHAR(300) NOT NULL DEFAULT '0' comment '应用回调地址',
  `apptime` BIGINT(11) NOT NULL DEFAULT 0 comment '应用修改时间',
  `isopen` int(1) not null default 1 comment '0关闭1开启',
  `organ_id` int not null default 0 comment '组织id',
   PRIMARY KEY(id)
) ENGINE=InnoDB;


--登陆用户数据表
CREATE TABLE `t_uservalid` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `asid` int default 0 comment '应用id',
  `unauthtoken` VARCHAR(200) DEFAULT '0' comment '未授权token',
  `authtoken` VARCHAR(200) DEFAULT '0' comment '授权token',
  `visittoken` VARCHAR(200) DEFAULT '0' comment '访问token',
  `unauthtokentime` BIGINT(11) default 0 comment '未授权token生成时间',
  `authtokentime` BIGINT(11) DEFAULT 0 comment '授权token生成时间',
  `visittokentime` BIGINT(11) DEFAULT 0 comment '访问toke生成时间',
  `userid` int comment '成员id',
  `info` int default 3 comment '成员信息等级',
  `isdel` char(1) default '1' comment '是否为未分组,1是0否',
   PRIMARY KEY(id)
) ENGINE=InnoDB;


---角色应用关系表
create table `t_role_appsecret` (
	`id` int NOT NULL AUTO_INCREMENT(11),
	`role_id` int(11) default 0 comment '角色id',
	`appsecret_id` int(11) default 0 comment '应用id',
	PRIMARY KEY(id);	
); 



/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
