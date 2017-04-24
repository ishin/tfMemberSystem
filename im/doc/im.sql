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
-- Database: `im`
--
CREATE DATABASE IF NOT EXISTS `im` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `im`;

-- --------------------------------------------------------

--
-- 表的结构 `t_group`：群组
--

CREATE TABLE `t_group` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(20),
  `name` VARCHAR(256),
  `createdate` VARCHAR(8),
  `creator_id` INT NOT NULL DEFAULT 0,
  `volume` INT NOT NULL DEFAULT 0 COMMENT '可容纳人数',
  `volumeuse` INT NOT NULL DEFAULT 0 COMMENT '已有人数',
  `space` INT NOT NULL DEFAULT 0 COMMENT '共享空间',
  `spaceuse` INT NOT NULL DEFAULT 0 COMMENT '已用共享空间',
  `annexlong` INT NOT NULL DEFAULT 0 COMMENT '聊天附件保留天数',
  `notice` VARCHAR(1024) COMMENT '群公告',
  `listorder` INT NOT NULL DEFAULT 0,
   PRIMARY KEY(id)
) ENGINE=InnoDB;

-- --------------------------------------------------------

--
-- 表的结构 `t_group_member`群组-成员关系
--

CREATE TABLE `t_group_member` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `group_id` INT NOT NULL DEFAULT 0,
  `member_id` INT NOT NULL DEFAULT 0,
  `is_creator` CHAR(1) DEFAULT '0' COMMENT '0非创建者，1创建者',
  `listorder` INT NOT NULL DEFAULT 0,
   PRIMARY KEY(id)
) ENGINE=InnoDB;

-- --------------------------------------------------------

--
-- 表的结构 `t_friend`：好友
--

CREATE TABLE `t_friend` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `member_id` INT NOT NOT NULL DEFAULT 0,
  `friend_id` INT NOT NOT NULL DEFAULT 0,
  `createdate` VARCHAR(8) NULL DEFAULT '0',
  `listorder` INT NOT NULL DEFAULT 0,
   PRIMARY KEY(id)
) ENGINE=InnoDB;

-- --------------------------------------------------------

--
-- 表的结构 `t_function`：辅助功能
--

CREATE TABLE `t_function` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(256) NOT NULL DEFAULT '0',
  `is_open` CHAR(1) NOT NULL DEFAULT '0',
  `listorder` INT NOT NULL DEFAULT 0,
   PRIMARY KEY(id)
) ENGINE=InnoDB;

-- --------------------------------------------------------

--
-- 表的结构 `t_dontdistrub`：群组消息免打扰
--

CREATE TABLE `t_dontdistrub` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `group_id` INT NOT NULL DEFAULT '0',
  `member_id` INT NOT NULL DEFAULT '0',
  `is_open` CHAR(1) NOT NULL DEFAULT '0',
  `listorder` INT NOT NULL DEFAULT 0,
   PRIMARY KEY(id)
) ENGINE=InnoDB;

-- --------------------------------------------------------
--
-- 表的结构 `t_map`：位置坐标表
--
CREATE TABLE `t_map` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL DEFAULT '0',
  `latitude` VARCHAR(50) NOT NULL DEFAULT '0',
  `longitude` VARCHAR(50) NOT NULL DEFAULT '0',
  `subdate` VARCHAR(15) NOT NULL DEFAULT '0',
   PRIMARY KEY(id)
) ENGINE=InnoDB;

--
-- 表的结构 `t_msgtop`：辅助功能
--

CREATE TABLE `t_msgtop` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL DEFAULT '0',
  `top_id` INT NOT NULL DEFAULT '0',
  `msg_type` CHAR(1) NOT NULL DEFAULT '0' COMMENT '1群组，2成员',
  `listorder` INT NOT NULL DEFAULT '0',
   PRIMARY KEY(id)
) ENGINE=InnoDB;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
