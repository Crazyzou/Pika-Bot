package com.example;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;

import com.example.game.core.GameCore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.*;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.console.data.PluginConfig;
import net.mamoe.mirai.contact.*;
import net.mamoe.mirai.event.events.*;
import net.mamoe.mirai.internal.deps.okhttp3.*;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;
import net.mamoe.mirai.utils.MiraiLogger;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public final class Demo extends JavaPlugin {

	public Demo() {
		super(new JvmPluginDescriptionBuilder("top.crazy_zou.kaqiu_bot", "0.3.1")
				.name("卡丘机器人")
				.author("z")
				.build());
	}
	public File basicDataFolder = getDataFolder();
	public File basicConfigFolder = getConfigFolder();
	public MiraiLogger logger = getLogger();
	@Override
	public void onEnable() {
		logger.info("插件开始加载，注册监听中。。。");
		GroupChange();
		GroupCommand();
		HeziSubscribe();
		logger.info("插件加载完毕");
	}
	// 这行代码其实没用
	// Bot bot = BotFactory.INSTANCE.newBot(2750250833L, "");
	public String role = output("role", "command.txt", 0);//元神接口角色
	public String spark = output("spark", "command.txt", 0);//星火语音文字
	static final int CONNECTION_TIMEOUT = 5000;
	static final int READ_TIMEOUT = 10000;
	static final int BUFFER_SIZE = 1024;
	ArrayList<String> stopReply = new ArrayList<>(Arrays.asList(
			"埋地雷", "签到", "统计"));
	private static final ExecutorService executor = Executors.newFixedThreadPool(2);

	// 盒子专用的监听注册
	public void HeziSubscribe(){
		GameCore.INSTANCE.initBaseFolder(basicDataFolder,basicConfigFolder);
		GlobalEventChannel.INSTANCE.registerListenerHost(new HeziListener());
	}

	// 群聊关键词总类
	public void GroupCommand() {

		// 监听消息+注册命令
		GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, event -> {
			String tag;
			String content = event.getMessage().contentToString();
			long userqq = event.getSender().getId();
			long groupId = event.getGroup().getId();
			String username = event.getSender().getNick();
			String permission = (event.getSender().getPermission()).toString();
			String mine = output(String.valueOf(groupId), "mine.txt", 1);//地雷判定开关
			String statue = output(String.valueOf(userqq), "superuser.txt", 1);

			if (groupId == 748546160L || groupId == 795327860L || groupId == 812531736L | groupId == 780594692L | groupId == 939196849L | groupId == 980929495L) {
				try {
					String reply = aojiaoreply(content);
					if (!reply.equals("null")) {
						String regex = "\\{[A-F0-9-]+\\}.";
						Pattern pattern = Pattern.compile(regex);
						Matcher matcher = pattern.matcher(reply);
						if (content.equals("题库")) {
							byte[] problem = wordTopicture(reply, "blue");
							Image image = event.getSubject().uploadImage(ExternalResource.create(problem));
							event.getSubject().sendMessage(image);
							countAdd();
						} else if (matcher.find()) {
							event.getSubject().sendMessage(Image.fromId(reply));
						} else {
							event.getSubject().sendMessage(reply);
						}
						countAdd();
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				if (mine.trim().equals("open") & permission.equals("MEMBER")) {
					try {
						String i = random(1, 10, 0);
						int j = Integer.parseInt(i);
						if (Integer.parseInt(i) >= 7) {
							mine = "close";
							event.getSender().mute(30 * j);
							event.getSubject().sendMessage("哈哈哈哈~\n[" + username + "]踩到了地雷，失去发言权[" + j / 2 + "]分钟");
							input(String.valueOf(groupId), "close", "mine.txt");
							countAdd();
						}
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
				switch (content) {
					case "菜单":
						try {
							String num = random(1, 5, 0);
							Image image = event.getSubject().uploadImage(ExternalResource.create(encodeImage("C:\\Users\\z\\Desktop\\BOT素材\\img\\菜单" + num + ".png")));

							MessageChainBuilder builder = new MessageChainBuilder();
							builder.append("『皮卡丘口令大全』\n");
							builder.append("= = = = = = = =\n");
							builder.append("签到・估价・统计・背包\n");
							builder.append("重开・退出・市场・打工\n");
							builder.append("星火・问答・炸弹・选择\n");
							builder.append("地雷・估价・统计・查号\n");
							builder.append("= = = = = = = =\n");
							builder.append(image);
							event.getSubject().sendMessage(builder.build());
							countAdd();
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						break;
					case "cs":
						try {
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
						break;
					case "签到":
						try {
							String reply = userinfo(String.valueOf(userqq));
							String[] user = reply.split(":");
							File imgFile =  new File(basicDataFolder,"img");
							if (user[0].equals("null")) {
								String num = random(1, 2, 0);
								ExternalResource res = ExternalResource.create(encodeImage(
										new File(imgFile,"已签到"+num+".png").toString()));
								Image image = event.getSubject().uploadImage(res);
								MessageChainBuilder builder = new MessageChainBuilder();
								builder.append("今天签过到了喵ᓚᘏᗢ\n");
								builder.append(image);
								event.getSubject().sendMessage(builder.build());
								res.close();
								countAdd();
							} else {
								String[] bag = bag(String.valueOf(userqq)).split(":");
								if (!bag[0].equals("null")) {
									String num = random(1, 4, 0);
									ExternalResource res = ExternalResource.create(encodeImage(
											new File(imgFile,"已签到"+num+".png").toString()));
									Image image = event.getSubject().uploadImage(res);
									MessageChainBuilder builder = new MessageChainBuilder();
									builder.add("『皮卡丘专属助手』\n");
									builder.add("⊱ ———*———⊰\n");
									builder.add("🚩[昵称]:" + username + "\n");
									builder.add("🚩[账号]:" + userqq + "\n");
									builder.add("🚩[地雷奖励]: " + user[0] + " 枚\n");
									builder.add("🚩[炸弹奖励]: " + user[1] + " 枚\n");
									builder.add("🚩[星币奖励]: " + user[2] + " 枚\n");
									builder.add("⊱ ———*———⊰\n");
									builder.add("🚩[指令]:背包\n");
									builder.add("🚩[指令]:埋地雷\n");
									builder.add("🚩[指令]:丢@\n");
									builder.add("⊱ ———*———⊰\n");
									builder.add(image);
									event.getSubject().sendMessage(builder.build());
									res.close();
								}
								countAdd();
								break;
							}
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
						break;
					case "核废水":
						try {
							String japan_water = japan_water();
							byte[] japan = wordTopicture("『皮卡丘专属助手』\n" +
									"= = = = = = = = = = = =\n" +
									japan_water +
									"\n= = = = = = = = = = = =", "blue");
							ExternalResource res = ExternalResource.create(japan);
							Image image = event.getSubject().uploadImage(res);
							event.getSubject().sendMessage(image);
							res.close();
							countAdd();
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						break;
					case "短视频":
						event.getSubject().sendMessage(
								"『皮卡丘专属助手』\n" +
										"= = = = = = = = = = = =\n" +
										"[甜妹视频]⚓[吊带系列]\n" +
										"[你的欲梦]⚓[JK系列]\n" +
										"[作者推荐]⚓[cos系列]\n" +
										"[玉足视频]⚓[清纯系列]\n" +
										"[热舞视频]⚓[慢摇系列]\n" +
										"[小哥哥视频]⚓[小姐姐视频]\n" +
										"\n= = = = = = = = = = = =");
						try {
							countAdd();
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						break;
					case "小哥哥视频":
						tag = "xgg";
						try {
							String videoFilePath = video_send(tag);
							ExternalResource externalResource = ExternalResource.create(new FileInputStream(videoFilePath));
							event.getSubject().getFiles().uploadNewFile("video.mp4", externalResource);
							externalResource.close(); // 释放资源
							File file = new File(videoFilePath);
							if (file.exists()) {
								file.delete();
							}
							countAdd();
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						break;
					case "小姐姐视频":
						tag = "xjj";
						try {
							String videoFilePath = video_send(tag);
							ExternalResource externalResource = ExternalResource.create(new FileInputStream(videoFilePath));
							event.getSubject().getFiles().uploadNewFile("video.mp4", externalResource);
							externalResource.close(); // 释放资源
							File file = new File(videoFilePath);
							if (file.exists()) {
								file.delete();
							}
							countAdd();
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						break;
					case "热舞视频":
						tag = "rewu";
						try {
							String videoFilePath = video_send(tag);
							ExternalResource externalResource = ExternalResource.create(new FileInputStream(videoFilePath));
							event.getSubject().getFiles().uploadNewFile("video.mp4", externalResource);
							externalResource.close(); // 释放资源
							File file = new File(videoFilePath);
							if (file.exists()) {
								file.delete();
							}
							countAdd();
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						break;
					case "你的欲梦":
						tag = "ndym";
						try {
							String videoFilePath = video_send(tag);
							ExternalResource externalResource = ExternalResource.create(new FileInputStream(videoFilePath));
							event.getSubject().getFiles().uploadNewFile("video.mp4", externalResource);
							externalResource.close(); // 释放资源
							File file = new File(videoFilePath);
							if (file.exists()) {
								file.delete();
							}
							countAdd();
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						break;
					case "jk系列":
					case "JK系列":
						tag = "jksp";
						try {
							String videoFilePath = video_send(tag);
							ExternalResource externalResource = ExternalResource.create(new FileInputStream(videoFilePath));
							event.getSubject().getFiles().uploadNewFile("video.mp4", externalResource);
							externalResource.close(); // 释放资源
							File file = new File(videoFilePath);
							if (file.exists()) {
								file.delete();
							}
							countAdd();
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						break;
					case "作者推荐":
						tag = "zzxjj";
						try {
							String videoFilePath = video_send(tag);
							ExternalResource externalResource = ExternalResource.create(new FileInputStream(videoFilePath));
							event.getSubject().getFiles().uploadNewFile("video.mp4", externalResource);
							externalResource.close(); // 释放资源
							File file = new File(videoFilePath);
							if (file.exists()) {
								file.delete();
							}
							countAdd();
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						break;
					case "cos系列":
					case "COS系列":
						tag = "COS";
						try {
							String videoFilePath = video_send(tag);
							ExternalResource externalResource = ExternalResource.create(new FileInputStream(videoFilePath));
							event.getSubject().getFiles().uploadNewFile("video.mp4", externalResource);
							externalResource.close(); // 释放资源
							File file = new File(videoFilePath);
							if (file.exists()) {
								file.delete();
							}
							countAdd();
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						break;
					case "甜妹视频":
						tag = "tianmei";
						try {
							String videoFilePath = video_send(tag);
							ExternalResource externalResource = ExternalResource.create(new FileInputStream(videoFilePath));
							event.getSubject().getFiles().uploadNewFile("video.mp4", externalResource);
							externalResource.close(); // 释放资源
							File file = new File(videoFilePath);
							if (file.exists()) {
								file.delete();
							}
							countAdd();
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						break;
					case "玉足视频":
						tag = "yuzu";
						try {
							String videoFilePath = video_send(tag);
							ExternalResource externalResource = ExternalResource.create(new FileInputStream(videoFilePath));
							event.getSubject().getFiles().uploadNewFile("video.mp4", externalResource);
							externalResource.close(); // 释放资源
							File file = new File(videoFilePath);
							if (file.exists()) {
								file.delete();
							}
							countAdd();
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						break;
					case "清纯系列":
						tag = "qingchun";
						try {
							String videoFilePath = video_send(tag);
							ExternalResource externalResource = ExternalResource.create(new FileInputStream(videoFilePath));
							event.getSubject().getFiles().uploadNewFile("video.mp4", externalResource);
							externalResource.close(); // 释放资源
							File file = new File(videoFilePath);
							if (file.exists()) {
								file.delete();
							}
							countAdd();
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						break;
					case "吊带系列":
						tag = "diaodai";
						try {
							String videoFilePath = video_send(tag);
							ExternalResource externalResource = ExternalResource.create(new FileInputStream(videoFilePath));
							event.getSubject().getFiles().uploadNewFile("video.mp4", externalResource);
							externalResource.close(); // 释放资源
							File file = new File(videoFilePath);
							if (file.exists()) {
								file.delete();
							}
							countAdd();
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						break;
					case "慢摇系列":
						tag = "manyao";
						try {
							String videoFilePath = video_send(tag);
							ExternalResource externalResource = ExternalResource.create(new FileInputStream(videoFilePath));
							event.getSubject().getFiles().uploadNewFile("video.mp4", externalResource);
							externalResource.close(); // 释放资源
							File file = new File(videoFilePath);
							if (file.exists()) {
								file.delete();
							}
							countAdd();
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						break;
					case "埋地雷":
						try {
							String user_mine = output(String.valueOf(userqq), "user-mine.txt", 1);
							File imgFile = new File(basicDataFolder,"img");
							if (user_mine.equals("close") | user_mine.equals("0")) {
								event.getSubject().sendMessage("🍥[" + username + "]你没有地雷了\n🍥[签到]每日可获取\n🍥[背包]可查看数量\n🍥[市场]可购买");
								break;
							}
							if (mine.trim().equals("close")) {
								String num = random(1, 5, 0);
								ExternalResource res = ExternalResource.create(encodeImage(
										new File(imgFile,"地雷"+num+".png").toString()));
								Image image = event.getSubject().uploadImage(res);
								MessageChainBuilder builder = new MessageChainBuilder();
								builder.append("[" + username + "]已埋好地雷啦，将随机引爆😋\n");
								builder.append(image);
								event.getSubject().sendMessage(builder.build());

								input(String.valueOf(groupId), "open", "mine.txt");
								input(String.valueOf(userqq), String.valueOf(Integer.parseInt(user_mine) - 1), "user-mine.txt");
								res.close();
								countAdd();
							}
							if (mine.trim().equals("open")) {
								event.getSubject().sendMessage("本群已有一颗地雷了啦🤔");
								countAdd();
							}
						} catch (Exception e) {
							event.getSubject().sendMessage("[" + username + "]你没有地雷了\n[签到]每日可获取\n[背包]可查看数量\n[市场]可购买");
						}
						break;
					case "统计":
						try {
							event.getSubject().sendMessage(data());
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						break;
					case "背包":
						try {
							File imgFile = new File(basicDataFolder,"img");
							String[] bag = bag(String.valueOf(userqq)).split(":");
							String num = null;
							num = random(1, 5, 0);
							ExternalResource res = ExternalResource.create(encodeImage(
									new File(imgFile,"地雷"+num+".png").toString()));
							Image image = event.getSubject().uploadImage(res);
							MessageChainBuilder messageBuilder = new MessageChainBuilder()
									.append("『皮卡丘专属助手』\n")
									.append("= = = = = = = =\n")
									.append("🥇[用户]:" + userqq + "\n")
									.append("🥇[地雷]:" + bag[0] + "\n")
									.append("🥇[炸弹]:" + bag[1] + "\n")
									.append("🥇[盾牌]:" + bag[3] + "\n")
									.append("🥇[星币]:" + bag[2] + "\n")
									.append("🥇[解禁卡]:" + bag[4] + "\n")
									.append("- - - - - - - -\n")
									.append("💎[S级荣誉]:〓" + bag[5] + "〓\n")
									.append("💎[A级荣誉]:〓" + bag[6] + "〓\n")
									.append("💎[B级荣誉]:〓" + bag[7] + "〓\n")
									.append("💎[C级荣誉]:〓" + bag[8] + "〓\n")
									.append("= = = = = = = =\n")
									.append(image);
							event.getSubject().sendMessage(messageBuilder.build());
							res.close();
							countAdd();
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						break;
					case "重开":
						try {
							countAdd();
							String output = output(String.valueOf(userqq), "people.txt", 0);
							if (output.equals("close")) {
								input(String.valueOf(userqq), "open", "people.txt");
								event.getSubject().sendMessage(
										"『皮卡丘专属助手』\n" +
												"= = = = = = = = = = = =\n" +
												"欢迎来到人生重开模拟器！\n" +
												"1、普通10连抽（小盒子）\n" +
												"2、豪华20连抽（随机）\n" +
												"每次选择请回复【选择+编号】选择天赋礼包。\n" +
												"回复【退出】可退出模拟器\n" +
												"= = = = = = = = = = = =");
								try {
									people("人生重开", username, String.valueOf(userqq));
								} catch (JsonProcessingException e) {
									throw new RuntimeException(e);
								}
							} else {
								event.getSubject().sendMessage(
										"『皮卡丘专属助手』\n" +
												"= = = = = = = = = = = =\n" +
												"上次重开还没有结束呢\n" +
												"= = = = = = = = = = = =");
							}
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						break;
					case "退出":
						input(String.valueOf(userqq), "close", "people.txt");
						event.getSubject().sendMessage(
								"『皮卡丘专属助手』\n" +
										"= = = = = = = = = = = =\n" +
										"已退出\n" +
										"= = = = = = = = = = = =");
						try {
							countAdd();
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						break;
					case "同意入群":
						if (!permission.equals("MEMBER") | userqq == 2937718557L) {
							try {
								GlobalEventChannel.INSTANCE.subscribeAlways(MemberJoinRequestEvent.class, joinEvent -> {
									joinEvent.accept();
								});
								countAdd();
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
						}
						break;
					case "审核":
						if (statue.equals("open")) {
							try {
								String word = check();
								byte[] check = wordTopicture("『皮卡丘专属助手』\n" +
										"= = = = = = = = = = = =\n" +
										word +
										"\n= = = = = = = = = = = =", "blue");
								ExternalResource res = ExternalResource.create(check);
								Image image = event.getSubject().uploadImage(res);
								event.getSubject().sendMessage(image);
								res.close();
								countAdd();
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
						} else {
							event.getSubject().sendMessage("你不是bot管理员，联系2937718557设置");
						}
						break;
					case "市场":
						event.getSubject().sendMessage(
								"『皮卡丘市场』\n" +
										"= = = = = = = = = = = =\n" +
										"🍟[地雷]:" + 5 + "星币\n" +
										"🍟[炸弹]:" + 7 + "星币\n" +
										"🍟[盾牌]:" + 7 + "星币\n" +
										"🍟[解禁卡]:" + 15 + "星币\n" +
										"🍟(买)(空格)(目标)(空格)(数量)\n" +
										"= = = = = = = = = = = =");
						try {
							countAdd();
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						break;
					case "捡瓶子":
						try {
							event.getSubject().sendMessage(getUrlWord("https://api.yujn.cn/api/shudong.php?type=text"));
							countAdd();
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						break;
					case "巴以冲突":
						try {
							String war = war();
							byte[] compete = wordTopicture(war, "blue");
							Image image = event.getSubject().uploadImage(ExternalResource.create(compete));
							event.getSubject().sendMessage(image);
							countAdd();
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						break;
					default:
						if (content.startsWith("查")) {
							String qq = content.split("查")[1];
							try {
								String response = find(qq);
								if (response.equals("查询失败")) {
									byte[] find = wordTopicture("『皮卡丘专属助手』\n" +
											"= = = = = = = = = = = =\n" +
											"未能查询到相关信息" +
											"\n= = = = = = = = = = = =", "blue");
									ExternalResource res = ExternalResource.create(find);
									Image image = event.getSubject().uploadImage(res);
									event.getSubject().sendMessage(image);
									res.close();
									countAdd();
								} else {
									String[] result = response.split(" ");
									String phone = result[0];
									String diqu = result[1];
									byte[] find = wordTopicture("『皮卡丘专属助手』\n" +
											"= = = = = = = = = = = =\n" +
											"[Phone]:" + phone + "\n" +
											"[Phone Location]:\n" + diqu +
											"\n= = = = = = = = = = = =", "blue");
									ExternalResource res = ExternalResource.create(find);
									Image image = event.getSubject().uploadImage(res);
									event.getSubject().sendMessage(image);
									res.close();
									countAdd();
								}
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
						}
						if (content.startsWith("反查")) {
							String phone = content.split("反查")[1];
							try {
								String response = twice_find(phone);
								if (response.equals("查询失败")) {
									byte[] find = wordTopicture("『皮卡丘专属助手』\n" +
											"= = = = = = = = = = = =\n" +
											"未能查询到相关信息" +
											"\n= = = = = = = = = = = =", "blue");
									ExternalResource res = ExternalResource.create(find);
									Image image = event.getSubject().uploadImage(res);
									event.getSubject().sendMessage(image);
									res.close();
									countAdd();
								} else {
									String[] result = response.split(" ");
									String find_qq = result[0];
									String diqu = result[1];
									byte[] find = wordTopicture("『皮卡丘专属助手』\n" +
											"= = = = = = = = = = = =\n" +
											"[QQ]:" + find_qq + "\n" +
											"[Phone Location]:\n" + diqu +
											"\n= = = = = = = = = = = =", "blue");
									ExternalResource res = ExternalResource.create(find);
									Image image = event.getSubject().uploadImage(res);
									event.getSubject().sendMessage(image);
									res.close();
									countAdd();
								}
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
						}
						if (content.startsWith("星火")) {
							try {
								String question = content.split("星火")[1];
								if (spark.equals("word")) {
									event.getSubject().sendMessage(
											"『皮卡丘专属助手』\n" +
													"= = = = = = = = = = = = \n" +
													"[用户提问]\n" + question + "\n" +
													"- - - - - - - - - - - - \n" +
													"[星火回答]\n" + Spark(question) + "\n" +
													"= = = = = = = = = = = = ");
									countAdd();
								}
								if (spark.equals("voice")) {
									ExternalResource audioResource = ExternalResource.create(new File(yuansheng("请你教简短但可以讲清基本内容且保证没有英文地回答" + question)));
									Audio audio = event.getSubject().uploadAudio(audioResource);
									event.getSubject().sendMessage("[用户提问]\n" + question);
									event.getSubject().sendMessage(audio);
									event.getSubject().sendMessage(username + " 收到了来自[" + role + "]的回答");
									audioResource.close();
									countAdd();
								}
							} catch (Exception e) {
								throw new RuntimeException(e);
							}
						}
						//设置
						if (content.startsWith("set")) {
							if (userqq == 2937718557L) {
								String[] command = content.split(" ");
								switch (command[1]) {
									case "role":
										input(command[1], command[2], "command.txt");
										role = command[2];
										event.getSubject().sendMessage("已切换语音接口角色为[" + command[2] + "]");
										break;
									case "spark":
										input(command[1], command[2], "command.txt");
										spark = command[2];
										event.getSubject().sendMessage("已切换星火回答为[" + command[2] + "]");
										break;
									default:
										if (command[1].matches("@\\d+")) {
											String qq = command[1].substring(1);
											String status = command[2];
											output(qq, "superuser.txt", 0);
											input(qq, status, "superuser.txt");
											event.getSubject().sendMessage("已设置[" + qq + "]超级用户权限:" + status);
										}
								}
							} else {
								event.getSubject().sendMessage("抱歉，该功能只有大主人才能使用");
							}
						}
						//查看
						if (content.startsWith("open")) {
							String[] command = content.split(" ");
							switch (command[1]) {
								case "role":
									try {
										File imgFile = new File(basicDataFolder,"voice.png");
										ExternalResource res = ExternalResource.create(encodeImage(imgFile.getPath()));
										Image image = event.getSubject().uploadImage(res);
										event.getSubject().sendMessage(image);
										res.close();
									} catch (IOException e) {
										throw new RuntimeException(e);
									}
							}
						}
						//学习
						if (content.startsWith("问")) {
							try {
								String regex = "^问\\s+(.*?)\\s+(.*?)$";
								Pattern pattern = Pattern.compile(regex);
								Matcher matcher = pattern.matcher(content);
								if (matcher.find()) {
									String question = matcher.group(1);
									String answer = matcher.group(2);
									if (answer.equals("[图片]")) {
										String word = event.getMessage().toString();
										String imageRegex = "image:([^,]+)";
										Pattern imagePattern = Pattern.compile(imageRegex);
										Matcher imageMatcher = imagePattern.matcher(word);
										if (imageMatcher.find()) {
											String match = imageMatcher.group(1);
											answer = match;
										}
									}
									if (stopReply.contains(question)) {
										event.getSubject().sendMessage("￣へ￣哼~不学这个！");
									} else {
										event.getSubject().sendMessage(
												"『皮卡丘学习ing』\n" +
														"= = = = = = = = = = = =\n" +
														"[用户设问]:\n" + question + "\n" +
														"- - - - - - - - - - - - \n" +
														"[用户设答]:\n" + answer + "\n" +
														"= = = = = = = = = = = =");
										study(String.valueOf(userqq), question, answer);
									}
									countAdd();
								}
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
						}
						if (content.startsWith("@")) {
							try {
								String pattern = "@(\\d+)\\s+(.*)";
								Pattern regex = Pattern.compile(pattern);
								Matcher matcher = regex.matcher(content);
								if (matcher.find()) {
									String qq = matcher.group(1);
									String word = matcher.group(2);
									String reply = aojiaoreply(word);
									if (qq.equals("2750250833")) {
										if (!reply.equals("null")) {
											event.getSubject().sendMessage(reply);
										} else {
											event.getSubject().sendMessage(
													"『皮卡丘学习ing』\n" +
															"= = = = = = = = = = = =\n" +
															"现已开通普通用户设问答:\n" +
															"格式:(问)(空格)(你的设问)(空格)(你的答案)\n" +
															"= = = = = = = = = = = =");
										}
										countAdd();
									}
									if (random(0, 10, 0).equals("1") && (!word.isEmpty())) {
										Image image = event.getSubject().uploadImage(ExternalResource.create(speak(qq, word)));
										event.getSubject().sendMessage(image);
										countAdd();
									}
								}
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
						}
						//估价
						if (content.startsWith("估价")) {
							try {
								String qq = content.split("估价")[1].trim();
								Image image = event.getSubject().uploadImage(ExternalResource.create(QQ_judge(Long.parseLong(qq))));
								event.getSubject().sendMessage(image);
								countAdd();
							} catch (NumberFormatException e) {
								event.getSubject().sendMessage("这不是数字叭...丘~");
							} catch (ArrayIndexOutOfBoundsException e) {
								try {
									Image image = event.getSubject().uploadImage(ExternalResource.create(QQ_judge(userqq)));
									event.getSubject().sendMessage(image);
									countAdd();
								} catch (IOException ex) {
									throw new RuntimeException(ex);
								}
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
						}
						//人生重开选择
						if (content.startsWith("选择")) {
							String choice = content.split("选择")[1].trim();
							String output = output(String.valueOf(userqq), "people.txt", 0);
							if (output.equals("close")) {
								event.getSubject().sendMessage(
										"『皮卡丘专属助手』\n" +
												"= = = = = = = = = = = =\n" +
												"发送【重开】来进行自己的重开之旅吧~\n" +
												"= = = = = = = = = = = =");
							} else {
								try {
									countAdd();
									event.getSubject().sendMessage(people(choice, username, String.valueOf(userqq)));
								} catch (JsonProcessingException e) {
									throw new RuntimeException(e);
								} catch (IOException e) {
									throw new RuntimeException(e);
								}
							}
						}
						//审核删除
						if (content.startsWith("删除")) {
							if (statue.equals("open")) {
								try {
									String[] parts = content.split("删除");
									if (parts.length > 1) {
										String num = parts[1].trim();
										if (!num.isEmpty()) {
											String row = readAndDeleteLine(num);
											event.getSubject().sendMessage("语料库[" + row + "]行删除成功！");
											delete(row);
											countAdd();
										} else {
											event.getSubject().sendMessage("请提供要删除的行号！");
										}
									} else {
										event.getSubject().sendMessage("请提供要删除的行号！");
									}
								} catch (IOException e) {
									throw new RuntimeException(e);
								}
							} else {
								event.getSubject().sendMessage("你不是bot管理员，联系2937718557设置");
							}
						}
						//丢炸弹
						if (content.startsWith("炸") | content.startsWith("丢")) {
							try {
								String pattern = "\\b[1-9][0-9]{4,10}\\b";
								Pattern regex = Pattern.compile(pattern);
								Matcher matcher = regex.matcher(content);
								String boom = output(String.valueOf(userqq), "user-boom.txt", 1);
								String shield = "0";
								String qq = "";

								if (matcher.find()) {
									qq = matcher.group();
									shield = output(String.valueOf(qq), "user-shield.txt", 1);
								} else {
									event.getSubject().sendMessage("丢的什么呢~卡丘看不懂");
								}
								if (boom.equals("0") | boom.equals("close")) {
									event.getSubject().sendMessage("🎪[" + username + "]你没有炸弹了\n🎪[签到]每日可获取\n🎪[背包]可查看数量\n🎪[市场]可购买");
								} else {
									String random = random(0, 9, 0);
									input(String.valueOf(userqq), String.valueOf(Integer.parseInt(boom) - 1), "user-boom.txt");
									Group group = event.getGroup();
									Member member = group.get(Long.parseLong(qq));
									String nick = member.getNick();
									if (member.getId() == 2655602003L) {
										if (random == "0") {
											random = "0";
										} else if (random == "1") {
											random = "4";
										} else if (random == "2") {
											random = "5";
										} else {
											random = "9";
										}
									}
									switch (random) {
										//丢中
										case "0":
										case "1":
										case "2":
										case "3":
										case "7":
										case "6":
											if (shield.equals("0")) {//有护盾
												switch (random) {
													case "0":
														event.getSubject().sendMessage("[" + username + "]武艺高超,一发入魂\n直接[炸]的[" + nick + "]小腹隆起");
														break;
													case "1":
														event.getSubject().sendMessage("[" + username + "]技艺精湛,一招制敌\n直接[炸]的[" + nick + "]下不了床");
														break;
													case "2":
													case "6":
														event.getSubject().sendMessage("[" + username + "]一剑封喉,霸气十足\n直接[炸]的[" + nick + "]只能扶墙");
														break;
													case "7":
													case "3":
														event.getSubject().sendMessage("[" + username + "]剑拔弩张,一发必中\n直接[炸]的[" + nick + "]狼狈不堪");
														break;
												}
												member.mute(60 * (Integer.parseInt(random) + 1));
											} else {//没有护盾
												input(qq, String.valueOf(Integer.parseInt(shield) - 1), "user-shield.txt");
												switch (random) {
													case "0":
														event.getSubject().sendMessage("[" + nick + "]不屑一顾的瞥了一眼[" + username + "]的小鸟\n用了小小一个[护盾]轻松挡住");
														break;
													case "1":
														event.getSubject().sendMessage("[" + nick + "]不屑一顾的瞥了一眼[" + username + "]的小鸟\n用[护盾]轻轻一挥手就将💣击落");
														break;
													case "2":
													case "6":
														event.getSubject().sendMessage("[" + nick + "]冷眼旁观，看着[" + username + "]挣扎着躲避挡住他💣的[护盾]\n因为不屑而毫无追击之意");
														break;
													case "7":
													case "3":
														event.getSubject().sendMessage("[" + nick + "]面无表情地看着[" + username + "]的小鸟被自己的[护盾]轻易摧毁，心中却没有丝毫得意之情");
														break;
												}
											}
											break;
										//没丢中
										case "4":
										case "5":
										case "8":
											switch (random) {
												case "4":
													event.getSubject().sendMessage("[" + username + "]贻笑大方,老眼昏花\n站在[" + nick + "]面前都没有丢中[炸弹]");
													break;
												case "8":
													event.getSubject().sendMessage("[" + username + "]自诩为[神炮手],看来不过如此\n连最简单的目标都无法命中,真是让人捧腹大笑");
													break;
												case "5":
													event.getSubject().sendMessage("[" + nick + "]不屑一顾,看着[" + username + "]手忙脚乱地[打炮],轻轻松松就躲了过去");
													break;
											}
											break;
										//反杀
										case "9":
											switch (random) {
												case "9":
													event.getSubject().sendMessage("[" + username + "]以为自己已经胜券在握\n却被[" + nick + "]逆袭推倒，让人大跌眼镜");
													break;
											}
											event.getSender().mute(60 * Integer.parseInt(random));
											break;
									}
								}
								countAdd();
							} catch (Exception e) {
								System.out.println(e);
							}
						}
						//购买
						if (content.startsWith("买")) {
							String[] buy = content.split(" ");
							String taget = buy[1];
							int boom = Integer.parseInt(output(String.valueOf(userqq), "user-boom.txt", 1));
							int mine_buy = Integer.parseInt(output(String.valueOf(userqq), "user-mine.txt", 1));
							int shield = Integer.parseInt(output(String.valueOf(userqq), "user-shield.txt", 1));
							int open_card = Integer.parseInt(output(String.valueOf(userqq), "user-open_card.txt", 1));
							int num = Integer.parseInt(buy[2]);
							int figure;
							int money = Integer.parseInt(output(String.valueOf(userqq), "user-money.txt", 1));
							switch (taget) {
								case "炸弹":
									figure = money - 7 * num;
									if (figure >= 0 & num >= 0) {
										event.getSubject().sendMessage("皮卡已经为主人已经买好了" + num + "枚💣啦");
										input(String.valueOf(userqq), String.valueOf(figure), "user-money.txt");
										input(String.valueOf(userqq), String.valueOf(boom + num), "user-boom.txt");
									} else {
										event.getSubject().sendMessage("你的星币不够买" + num + "枚💣了啦\n(打工)(空格)(0-9)可获得星币");
									}
									break;
								case "地雷":
									figure = money - 5 * num;
									if (figure >= 0 & num >= 0) {
										event.getSubject().sendMessage("皮卡已经为主人已经买好了" + num + "枚地雷啦");
										input(String.valueOf(userqq), String.valueOf(figure), "user-money.txt");
										input(String.valueOf(userqq), String.valueOf(mine_buy + num), "user-mine.txt");
									} else {
										event.getSubject().sendMessage("你的星币不够买" + num + "枚地雷了啦\n(打工)(空格)(0-9)可获得星币");
									}
									break;
								case "盾牌":
									figure = money - 7 * num;
									if (figure > 0 & num >= 0) {
										event.getSubject().sendMessage("皮卡已经为主人已经买好了" + num + "枚盾牌啦");
										input(String.valueOf(userqq), String.valueOf(figure), "user-money.txt");
										input(String.valueOf(userqq), String.valueOf(shield + num), "user-shield.txt");
									} else {
										event.getSubject().sendMessage("你的星币不够买" + num + "枚盾牌了啦\n(打工)(空格)(数字)可获得星币");
									}
									break;
								case "解禁卡":
									figure = money - 15 * num;
									if (figure > 0 & num >= 0) {
										event.getSubject().sendMessage("皮卡已经为主人已经买好了" + num + "张解禁卡啦");
										input(String.valueOf(userqq), String.valueOf(figure), "user-money.txt");
										input(String.valueOf(userqq), String.valueOf(open_card + num), "user-open_card.txt");
									} else {
										event.getSubject().sendMessage("你的星币不够买" + num + "张解禁卡了啦\n(打工)(空格)(数字)可获得星币");
									}
									break;
							}
							try {
								countAdd();
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
						}
						if (content.startsWith("打工")) {
							if (mine.trim().equals("open")) {
								event.getSubject().sendMessage("（￣︶￣）↗　群里面有颗地雷咋打工呀~");
							} else {
								int add = Integer.parseInt(content.split(" ")[1]);
								String money = output(String.valueOf(userqq), "user-money.txt", 1);
								if (money.equals("close")) {
									event.getSubject().sendMessage("你是第一次来打工吧,先[签到]录入信息才能打工啦~");
								} else {
									if (add <= 9 && add >= 1) {
										try {
											String num = random(3, 6, 0);
											event.getSender().mute(180 * Integer.parseInt(num));
											File imgFolder = new File(basicDataFolder,"img");
											String imgPath = new File(imgFolder,"work" + num + ".jpg").toString();
											ExternalResource res = ExternalResource.create(encodeImage(imgPath));
											Image image = event.getSubject().uploadImage(res);
											ExternalResource qRes = ExternalResource.create(
													wordTopicture(aojiaoreply("题库"), "blue"));
											Image picture = event.getSubject().uploadImage(qRes);
											MessageChainBuilder messageBuilder = new MessageChainBuilder()
													.append("呐呐呐~你的背包到账[")
													.append(String.valueOf(add))
													.append("]枚星币啦\n")
													.append(image)
													.append("\n来都来辣做道题叭ฅʕ •̫͡•ʔฅ\n")
													.append(picture);
											event.getSubject().sendMessage(messageBuilder.build());
											input(String.valueOf(userqq), String.valueOf(add + Integer.parseInt(money)), "user-money.txt");
											res.close();
											qRes.close();
											countAdd();
										} catch (IOException e) {
											throw new RuntimeException(e);
										}
									} else {
										event.getSubject().sendMessage("怎么能打工这么久呢,1-9分钟就够了啦");
									}
								}
							}
						}
						if (content.startsWith("访问")) {
							String[] parts = content.split("访问");
							String url = parts[1];
							try {
								event.getSubject().sendMessage(getUrlWord(url));
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
						}
						//解禁卡
						if (content.startsWith("解")) {
							try {
								String pattern = "\\b[1-9][0-9]{4,10}\\b";
								Pattern regex = Pattern.compile(pattern);
								Matcher matcher = regex.matcher(content);
								String card = output(String.valueOf(userqq), "user-open_card.txt", 1);
								String qq = "";
								if (matcher.find()) {
									qq = matcher.group();
								}
								if (card.equals("0")) {
									event.getSubject().sendMessage("\uD83C\uDFAA[" + username + "]你没有解禁卡了\n\uD83C\uDFAA[背包]可查看数量\n\uD83C\uDFAA[市场]可购买");
								} else {
									input(String.valueOf(userqq), String.valueOf(Integer.parseInt(card) - 1), "user-open_card.txt");
									Group group = event.getGroup();
									Member member = group.get(Long.parseLong(qq));
									String nick = member.getNick();
									member.mute(1);
									event.getSubject().sendMessage("⚛️已经为主人解禁" + nick);
								}
								countAdd();
							} catch (Exception e) {
								System.out.print("解禁卡出错");
							}
						}
						//充值
						if (content.startsWith("充") & userqq == 2750250833L) {
							String pattern = "\\b[1-9][0-9]{4,10}\\b";
							Pattern regex = Pattern.compile(pattern);
							Matcher matcher = regex.matcher(content);
							String qq = "";
							if (matcher.find()) {
								qq = matcher.group();
							}
							input(qq, "50", "user-money.txt");
							event.getSubject().sendMessage("已充值50枚星币");
						}
						if (content.startsWith("点歌")) {
							String songName = content.substring(2);
							CompletableFuture<String> songFuture = CompletableFuture.supplyAsync(() -> {
								try {
									return song(songName);
								} catch (UnsupportedEncodingException e) {
									throw new RuntimeException(e);
								}
							});
							songFuture.thenAcceptAsync(result -> {
								if (!result.equals("false")) {
									try {
										String[] list = result.split(";");
										Image image = event.getSubject().uploadImage(ExternalResource.create(urlTopng(list[2])));
										ExternalResource audioResource = ExternalResource.create(new File("C:\\Users\\z\\Desktop\\BOT素材\\song.amr"));
										Audio audio = event.getSubject().uploadAudio(audioResource);
										MessageChainBuilder messages = new MessageChainBuilder()
												.append("[歌集]:" + list[0])
												.append("\n[歌手]:" + list[3])
												.append("\n[歌名]:" + list[1])
												.append(image);
										event.getSubject().sendMessage(messages.build());
										event.getSubject().sendMessage(audio);
									} catch (IOException e) {
										throw new RuntimeException(e);
									}
								} else {
									event.getSubject().sendMessage("点歌失败，请检查api");
								}
							});
						}
				}
			}
		});
	}

	//群聊动态监听
	public void GroupChange() {
		//群员离群加群检测
		GlobalEventChannel.INSTANCE.subscribeAlways(GroupMemberEvent.class, groupMemberEvent -> {
			long qq = groupMemberEvent.getMember().getId();
			long groupId = groupMemberEvent.getGroup().getId();
			if (groupId == 812531736L | groupId == 780594692L | groupId == 939196849L) {
				if (groupMemberEvent instanceof MemberJoinEvent) {
					String response = user_message(qq);
					String[] user_message = response.split(":");

					String nick = user_message[0];
					String sex = user_message[1];
					String signature = user_message[2];
					//int likeCount = Integer.parseInt(user_message[3]);
					String qqLevel = user_message[3];
					String qid = user_message[4];
					String expertDays = user_message[5];
					//String onlineStatus = user_message[7];
					//int visitorCount = Integer.parseInt(user_message[8]);

					byte[] join = new byte[0];
					try {
						join = wordTopicture("『皮卡丘专属助手』\n" +
								"新人进群提示--------------\n" +
								"[昵称]:" + nick + "\n" +
								"[账号]:" + qq + "\n" +
								"[性别]:" + sex + "\n" +
								"[等级]:" + qqLevel + "\n" +
								"[QID]:" + qid + "\n" +
								"[达人天数]:" + expertDays + "\n" +
								//"[名片赞数]:" + likeCount +"\n" +
								//"[空间访客]:" + visitorCount +"\n" +
								//"[在线状态]:\n" + onlineStatus +"\n" +
								"[个性签名]:\n" + signature + "\n" +
								"新人进群提示--------------", "blue");
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
					Image tailimage = groupMemberEvent.getMember().uploadImage(ExternalResource.create(join));
					groupMemberEvent.getGroup().sendMessage(tailimage);
				}
				if (groupMemberEvent instanceof MemberLeaveEvent) {
					String nick = groupMemberEvent.getMember().getNick();

					byte[] join = new byte[0];
					try {
						join = wordTopicture("『皮卡丘专属助手』\n" +
								"群友离群提示--------------\n" +
								"[昵称]:" + nick + "\n" +
								"[账号]:" + qq + "\n" +
								"群友离群提示--------------", "blue");
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
					Image tailimage = groupMemberEvent.getMember().uploadImage(ExternalResource.create(join));
					groupMemberEvent.getGroup().sendMessage(tailimage);
				}
			}
		});

		//群聊名片更改监听
		GlobalEventChannel.INSTANCE.subscribeAlways(MemberCardChangeEvent.class, memberCardChangeEvent -> {
			long userId = memberCardChangeEvent.getUser().getId();
			String oldname = memberCardChangeEvent.getOrigin();
			String newname = memberCardChangeEvent.getNew();
			LocalDateTime now = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String time = now.format(formatter);
			long groupId = memberCardChangeEvent.getGroup().getId();
			if (groupId == 812531736L | groupId == 780594692L | groupId == 939196849L) {
				try {
					if (!(newname.isEmpty()) & !(oldname.isEmpty())) {
						byte[] card_change = wordTopicture("『皮卡丘专属助手』\n" +
								"= = = = = = = = = = = =\n" +
								"检测到群里有一货改名啦\n" +
								"╔[成员号]:" + userId + "\n" +
								"╟[旧名片]:" + oldname + "\n" +
								"╟[新名片]:" + newname + "\n" +
								"╚" + time +
								"\n= = = = = = = = = = = =", "blue");
						Image tailimage = memberCardChangeEvent.getMember().uploadImage(ExternalResource.create(card_change));
						memberCardChangeEvent.getGroup().sendMessage(tailimage);
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});

		//申请加群
		GlobalEventChannel.INSTANCE.subscribeAlways(MemberJoinRequestEvent.class, joinEvent -> {
			long groupId = joinEvent.getGroup().getId();
			if (groupId == 812531736L | groupId == 780594692L | groupId == 939196849L) {
				if (joinEvent instanceof MemberJoinRequestEvent) {
					long qq = joinEvent.getFromId();
					String name = joinEvent.getFromNick();
					String word = joinEvent.getMessage();
					joinEvent.getGroup().sendMessage(
							"『皮卡丘专属助手』\n" +
									"= = = = = = = = = = = =\n" +
									"有人申请入群\n" +
									"[账号]:" + qq + "\n" +
									"[昵称]:" + name + "\n" +
									word + "\n" +
									"= = = = = = = = = = = =");
				}
			}
		});
	}

	//信息获取
	public String user_message(long user_qq) {
		try {
			URL url = new URL("https://api.pearktrue.cn/api/qq/query.php?qq=" + user_qq);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			// 设置请求方式
			connection.setRequestMethod("GET");
			connection.connect();

			// 获取响应码
			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
				StringBuilder response = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					response.append(line);
				}
				reader.close();

				//解析json数据
				Gson gson = new Gson();
				JsonObject jsonObject = gson.fromJson(response.toString(), JsonObject.class);
				JsonObject dataObject = jsonObject.getAsJsonObject("data");
				String nick = dataObject.get("nickname").getAsString();
				String sex = dataObject.get("sex").getAsString();
				String signature = dataObject.get("sign").getAsString();
				String qqLevel = dataObject.get("level").getAsString();
				String qid = dataObject.get("qid").getAsString();
				String expertDays = dataObject.get("login_days").getAsString();

				return nick + ":" + sex + ":" + signature + ":" + qqLevel + ":" + qid + ":" + expertDays;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "接口信息错误请检查";
	}

	//qq估值
	public byte[] QQ_judge(long user_qq) throws IOException {
		String urlString = " http://api.yujn.cn/api/pinggu.php?qq=" + URLEncoder.encode(String.valueOf(user_qq), "UTF-8");
		return urlTopng(urlString);
	}

	//qq查绑
	public String find(String qq) throws IOException {
		String findUrl = "https://api.yujn.cn/api/chaq.php?qq=" + qq;
		URL url = new URL(findUrl);
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.connect();

		// 读取json数据
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		reader.close();

		// 解析json数据
		JsonObject jsonObject = null;
		try {
			jsonObject = JsonParser.parseString(sb.toString()).getAsJsonObject();
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
			return "查询失败";
		}
		if (jsonObject != null && jsonObject.has("status") && jsonObject.get("status").getAsInt() == 500) {
			return "查询失败";
		}

		// 提取所需信息
		String Phone = jsonObject != null && jsonObject.has("phone") ? jsonObject.get("phone").getAsString() : "";
		String Phone_Location = jsonObject != null && jsonObject.has("phonediqu") ? jsonObject.get("phonediqu").getAsString() : "";

		return Phone + " " + Phone_Location;
	}

	//手机号查绑
	public String twice_find(String phone) throws IOException {
		String findUrl = "https://zy.xywlapi.cc/qqphone?phone=" + phone;
		URL url = new URL(findUrl);
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.connect();

		// 读取json数据
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		reader.close();

		// 解析json数据
		JsonObject jsonObject = null;
		try {
			jsonObject = JsonParser.parseString(sb.toString()).getAsJsonObject();
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
			return "查询失败";
		}
		if (jsonObject != null && jsonObject.has("status") && jsonObject.get("status").getAsInt() == 500) {
			return "查询失败";
		}

		// 提取所需信息
		String qq = jsonObject != null && jsonObject.has("qq") ? jsonObject.get("qq").getAsString() : "";
		String Phone_Location = jsonObject != null && jsonObject.has("phonediqu") ? jsonObject.get("phonediqu").getAsString() : "";

		return qq + " " + Phone_Location;
	}

	//小日本
	public String japan_water() throws IOException {
		// 创建URL对象
		URL url = new URL("https://api.yujn.cn/api/sb_Japan.php?count=10&type=json");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		int responseCode = connection.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			String line;
			StringBuilder response = new StringBuilder();

			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();

			// 解析JSON数据
			JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
			JsonArray jsonArray = jsonObject.getAsJsonArray("data");

			StringBuilder result = new StringBuilder();
			for (JsonElement element : jsonArray) {
				JsonObject itemObject = element.getAsJsonObject();
				String time = itemObject.get("time").getAsString();
				String title = itemObject.get("title").getAsString();
				result.append("[").append(time).append("]").append("\n").append(title).append("\n--------------\n");
			}

			return result.toString();
		}
		return "接口错误";
	}

	//巴以冲突
	public String war() throws IOException {
		String urlString = "https://api.yujn.cn/api/bayi.php?count=10";
		return getUrlWord(urlString);
	}

	//视频通用解析
	public String video_send(String tag) throws IOException {
		String line;
		String filePath = "C:\\Users\\z\\Desktop\\BOT素材\\video.mp4";
		URL url = new URL("http://api.yujn.cn/api/" + tag + ".php?type=text");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		connection.setRequestMethod("GET");
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
		StringBuilder response = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			response.append(line);
		}
		reader.close();

		FileUtils.copyURLToFile(new URL(response.toString()), new File(filePath));

		return filePath;
	}

	//星火
	public static String Spark(String question) throws Exception {
		final List<String> answerList = new ArrayList<>();
		String hostUrl = "https://spark-api.xf-yun.com/v2.1/chat";
		String apiKey = "";
		String apiSecret = "";
		String authUrl = getAuthUrl(hostUrl, apiKey, apiSecret);
		String appid = "cc36e4de";
		OkHttpClient client = new OkHttpClient.Builder().build();
		String url = authUrl.replace("http://", "ws://").replace("https://", "wss://");
		Request request = new Request.Builder().url(url).build();
		CountDownLatch latch = new CountDownLatch(1);

		WebSocket webSocket = client.newWebSocket(request, new WebSocketListener() {
			@Override
			public void onOpen(WebSocket webSocket, Response response) {
				// 发送问题
				JsonObject requestJson = new JsonObject();

				JsonObject header = new JsonObject();
				header.addProperty("app_id", appid);
				header.addProperty("uid", UUID.randomUUID().toString().substring(0, 10));

				JsonObject parameter = new JsonObject();
				JsonObject chat = new JsonObject();
				chat.addProperty("domain", "generalv2");
				chat.addProperty("temperature", 0.5);
				chat.addProperty("max_tokens", 4096);
				parameter.add("chat", chat);

				JsonObject payload = new JsonObject();
				JsonObject message = new JsonObject();
				JsonArray text = new JsonArray();
				RoleContent roleContent = new RoleContent();
				roleContent.role = "user";
				roleContent.content = question;
				text.add(new Gson().toJsonTree(roleContent));
				message.add("text", text);
				payload.add("message", message);
				requestJson.add("header", header);
				requestJson.add("parameter", parameter);
				requestJson.add("payload", payload);

				String jsonString = new Gson().toJson(requestJson);
				webSocket.send(jsonString);
			}

			@Override
			public void onMessage(WebSocket webSocket, String text) {
				Gson gson = new Gson();
				JsonParse myJsonParse = gson.fromJson(text, JsonParse.class);
				if (myJsonParse.header.code != 0) {
					System.out.println("发生错误，错误码为：" + myJsonParse.header.code);
					System.out.println("本次请求的sid为：" + myJsonParse.header.sid);
					webSocket.close(1000, "");
				}
				List<Text> textList = myJsonParse.payload.choices.text;
				for (Text temp : textList) {
					answerList.add(temp.content);
				}
				if (myJsonParse.header.status == 2) {
					webSocket.close(1000, "");
					latch.countDown();
				}
			}
		});

		latch.await();
		StringBuilder sparkAnswerBuilder = new StringBuilder();
		for (String answer : answerList) {
			sparkAnswerBuilder.append(answer);
		}
		return sparkAnswerBuilder.toString();
	}

	//星火url鉴权
	public static String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
		URL url = new URL(hostUrl);
		// 时间
		SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
		format.setTimeZone(TimeZone.getTimeZone("GMT"));
		String date = format.format(new Date());
		// 拼接
		String preStr = "host: " + url.getHost() + "\n" +
				"date: " + date + "\n" +
				"GET " + url.getPath() + " HTTP/1.1";
		// SHA256加密
		Mac mac = Mac.getInstance("hmacsha256");
		SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "hmacsha256");
		mac.init(spec);

		byte[] hexDigits = mac.doFinal(preStr.getBytes(StandardCharsets.UTF_8));
		// Base64加密
		String sha = Base64.getEncoder().encodeToString(hexDigits);
		// 拼接
		String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
		// 拼接地址
		HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse("https://" + url.getHost() + url.getPath())).newBuilder().//
				addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8))).//
				addQueryParameter("date", date).//
				addQueryParameter("host", url.getHost()).//
				build();

		return httpUrl.toString();
	}

	//原神语音接口
	public String yuansheng(String preset) throws Exception {
		String filePath = "C:\\Users\\z\\Desktop\\BOT素材\\voice.silk";
		String content = Spark(preset);
		String urlString = "https://xiaobai.klizi.cn/API/other/yuanshen_tts.php?speaker=" +
				URLEncoder.encode(role, "UTF-8") +
				"&text=" + URLEncoder.encode(content, "UTF-8") +
				"&noise=0.90&length=1";
		URL url = new URL(urlString);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");

		try (InputStream inputStream = connection.getInputStream();
		     FileOutputStream outputStream = new FileOutputStream(filePath)) {
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
		}

		File file = new File(filePath);
		String newFilePath = filePath.substring(0, filePath.lastIndexOf('.')) + ".silk";
		file.renameTo(new File(newFilePath));

		return newFilePath;
	}

	//签到
	public String userinfo(String userqq) throws IOException {
		String mineStr = output(userqq, "user-mine.txt", 1).trim();
		String boomStr = output(userqq, "user-boom.txt", 1).trim();
		String moneyStr = output(userqq, "user-money.txt", 1).trim();
		String lastTime = output(userqq, "user-time.txt", 1).trim();

		if (lastTime.equals(getCurrentDate())) {
			return "null";
		} else {
			int mine = Integer.parseInt(mineStr);
			int boom = Integer.parseInt(boomStr);
			int money = Integer.parseInt(moneyStr);
			String addMine = random(1, 4, 0);
			String addBoom = random(1, 3, 0);
			String addMoney = random(10, 20, 0);

			int newMine = mine + Integer.parseInt(addMine);
			int newBoom = boom + Integer.parseInt(addBoom);
			int newMoney = money + Integer.parseInt(addMoney);
			input(userqq, String.valueOf(newMine), "user-mine.txt");
			input(userqq, String.valueOf(newBoom), "user-boom.txt");
			input(userqq, String.valueOf(newMoney), "user-money.txt");
			input(userqq, getCurrentDate(), "user-time.txt");

			return addMine + ":" + addBoom + ":" + addMoney;
		}
	}

	//背包
	public String bag(String userqq) {
		String mineStr = output(userqq, "user-mine.txt", 1);
		String boomStr = output(userqq, "user-boom.txt", 1);
		String moneyStr = output(userqq, "user-money.txt", 1);
		String lastTime = output(userqq, "user-time.txt", 1);
		String shieldStr = output(userqq, "user-shield.txt", 1);
		String open_cardStr = output(userqq, "user-open_card.txt", 1);
		String SStr = output(userqq, "user-S.txt", 1);
		String AStr = output(userqq, "user-A.txt", 1);
		String BStr = output(userqq, "user-B.txt", 1);
		String CStr = output(userqq, "user-C.txt", 1);
		if (mineStr.equals("close") || mineStr.isEmpty() || boomStr.equals("close") || boomStr.isEmpty() || moneyStr.equals("close") || lastTime.equals("close")) {
			return "0:0:0:0:0:0:0:0:0";
		}
		int mine = Integer.parseInt(mineStr);
		int boom = Integer.parseInt(boomStr);
		int money = Integer.parseInt(moneyStr);
		int shield = Integer.parseInt(shieldStr);
		int open_card = Integer.parseInt(open_cardStr);
		int S = Integer.parseInt(SStr);
		int A = Integer.parseInt(AStr);
		int B = Integer.parseInt(BStr);
		int C = Integer.parseInt(CStr);
		return mine + ":" + boom + ":" + money + ":" + shield + ":" + open_card + ":" + S + ":" + A + ":" + B + ":" + C;
	}

	//GET-url处理为png字节数组
	public byte[] urlTopng(String http) throws IOException {
		try {
			URL url = new URL(http);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.connect();

			InputStream inputStream = conn.getInputStream();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[4096];
			int n = 0;
			while ((n = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, n);
			}
			byte[] imageBytes = outputStream.toByteArray();

			// 将图片转换成 png 格式
			BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));

			inputStream.close();
			outputStream.close();

			return imageToPngBytes(image);

		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	//将文字处理成微软雅黑的颜色字体
	public byte[] wordTopicture(String content, String color) throws IOException {//
		String urlString = "http://api.setbug.com/tools/text2image/?text=" + URLEncoder.encode(content, "UTF-8") /*+ "&ys=" + URLEncoder.encode(color, "UTF-8")*/;
		return urlTopng(urlString);
	}

	//传入路径并处理返回
	public static byte[] encodeImage(String imagePath) throws IOException {
		File file = new File(imagePath);
		FileInputStream imageInFile = new FileInputStream(file);
		byte[] imageData = new byte[(int) file.length()];
		imageInFile.read(imageData);

		String encodedImage = Base64.getEncoder().encodeToString(imageData);
		byte[] decodedImage = Base64.getDecoder().decode(encodedImage);

		BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(decodedImage));

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, "png", baos);
		byte[] pngData = baos.toByteArray();

		// 返回PNG格式的字节数组
		return pngData;
	}

	//图片字节转png字节
	public byte[] imageToPngBytes(BufferedImage image) {
		try {
			ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
			ImageIO.write(image, "png", pngOutputStream);
			return pngOutputStream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	//api获取随机数
	public String random(int min, int max, int flag) throws IOException {
		long timestamp = System.currentTimeMillis();
		int range = max - min + 1;
		int randomNum = (int) ((timestamp % range) + min);

		return String.valueOf(randomNum);
	}

	//读取
	public String output(String head, String path, int i) {
		try (BufferedReader reader = new BufferedReader(new FileReader(new File(basicDataFolder,path)))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(":");
				if (parts[0].trim().equals(head)) {
					return parts[1].trim();
				}
			}

			//如果没有找到匹配的head，则写入新的行
			FileWriter fileWriter = new FileWriter(new File(basicDataFolder,path), true);
			BufferedWriter writer = new BufferedWriter(fileWriter);
			if (i == 0) {
				writer.write(head + ":close");
			} else {
				writer.write(head + ":0");
			}
			writer.newLine();
			writer.close();
		} catch (IOException e) {
			System.out.println("注册中......");
		}
		return "close";
	}

	//写入
	public void input(String head, String value, String path) {
		try (BufferedReader reader = new BufferedReader(new FileReader(new File(basicDataFolder,path)))) {
			StringBuilder newContent = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(":");
				if (parts[0].trim().equals(head)) {
					line = parts[0].trim() + ":" + value.trim();
				}
				newContent.append(line).append(System.lineSeparator());
			}

			try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(basicDataFolder,path)))) {
				writer.write(newContent.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//词库回复
	public String aojiaoreply(String talk) throws IOException {
		String filePath = new File(basicDataFolder,"可爱词库.xlsx").toString();
		String reply = "null";

		try (FileInputStream fis = new FileInputStream(filePath);
		     Workbook workbook = new XSSFWorkbook(fis)) {
			Sheet sheet = workbook.getSheetAt(0);
			List<Row> matchingRows = new ArrayList<>();

			for (Row row : sheet) {
				Cell cell = row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
				if (cell != null && cell.getCellType() == CellType.STRING) {
					String cellValue = cell.getStringCellValue();
					if (cellValue.equals(talk)) {
						matchingRows.add(row);
					}
				}
			}

			if (!matchingRows.isEmpty()) {
				int randomIndex = Integer.parseInt(random(0, matchingRows.size() - 1, 0));
				Row selectedRow = matchingRows.get(randomIndex);
				reply = selectedRow.getCell(1).getStringCellValue();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return reply;
	}

	//speak
	public byte[] speak(String qq, String content) throws IOException {
		String url = "https://xiaobai.klizi.cn/API/tw/QQChatFrame.php?data={\"list\":[{\"uin\":\"" + URLEncoder.encode(qq, "UTF-8") + "\",\"message\":\"" + URLEncoder.encode(content, "UTF-8") + "\"}]}";
		return urlTopng(url);
	}

	//调用数据报告
	public String data() throws IOException {
		File file = new File(basicDataFolder, "data.txt");
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		String line;
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line).append("\n");
		}
		reader.close();
		return stringBuilder.toString();
	}

	//学习
	public void study(String qq, String question, String answer) {
		try {
			String filePath = new File(basicDataFolder,"可爱词库.xlsx").toString();
			String filePath2 = new File(basicDataFolder,"check.txt").toString();

			// 备份原始的 "可爱词库.xlsx"
			Files.copy(Paths.get(filePath), Paths.get(basicDataFolder.toString(),"可爱词库_backup.xlsx"), StandardCopyOption.REPLACE_EXISTING);

			File file = new File(filePath);
			FileInputStream fis = new FileInputStream(file);
			Workbook workbook = WorkbookFactory.create(fis);
			Sheet sheet = workbook.getSheetAt(0);
			int lastRowNum = sheet.getLastRowNum();
			Row newRow = sheet.createRow(lastRowNum + 1);
			Cell questionCell = newRow.createCell(0);
			questionCell.setCellValue(question);
			Cell answerCell = newRow.createCell(1);
			answerCell.setCellValue(answer);

			FileOutputStream fos = new FileOutputStream(file);
			workbook.write(fos);

			// 写入 check.txt 文件
			BufferedWriter writer = new BufferedWriter(new FileWriter(filePath2, true));
			String data = "#" + (lastRowNum + 1) + " : \n[账户]" + qq + " : \n[问]" + question + " \n[答]" + answer + "\n\n";
			writer.write(data);
			writer.newLine();
			writer.close();

			fis.close();
			fos.close();
			System.out.println("写入成功！");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("写入失败！");
		}
	}

	//获取文件行数
	/*public int getLineNumber(String filePath) throws IOException {
		int lineNumber = 1;
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		while (reader.readLine() != null) {
			lineNumber++;
		}
		reader.close();
		return lineNumber;
	}*/

	//调用计数
	public void countAdd() throws IOException {
		File file = new File(basicDataFolder,"data.txt");
		int total = 0;
		int yesterday = 0;
		int today = 0;
		String lastTime = "";

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		String line;
		while ((line = reader.readLine()) != null) {
			String[] parts = line.split(":");
			switch (parts[0]) {
				case "总调用量":
					total = Integer.parseInt(parts[1].trim()) + 1;
					break;
				case "昨日调用量":
					yesterday = Integer.parseInt(parts[1].trim());
					break;
				case "今日调用量":
					today = Integer.parseInt(parts[1].trim());
					break;
				case "上次调用日期":
					lastTime = parts[1];
					break;
			}
		}
		reader.close();

		if (lastTime.equals(getCurrentDate())) {
			today++;
		} else {
			yesterday = today;
			today = 1;
		}

		PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
		writer.println("总调用量:" + total);
		writer.println("昨日调用量:" + yesterday);
		writer.println("今日调用量:" + today);
		writer.println("上次调用日期:" + getCurrentDate());
		writer.close();
	}

	//人生重开模拟器
	public static String people(String choice, String username, String user_num) throws JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Api-Key", "b742299ery2n9wgm");
		headers.set("Api-Secret", "sdowrpu7");

		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode body = objectMapper.createObjectNode();
		body.put("content", choice);
		body.put("type", 1);
		body.put("from", user_num);
		body.put("fromName", username);

		HttpEntity<ObjectNode> requestEntity = new HttpEntity<>(body, headers);

		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<String> responseEntity = restTemplate.postForEntity(
				"https://api.mlyai.com/reply",
				requestEntity,
				String.class
		);

		String response = responseEntity.getBody();

		// 解析响应，提取content部分
		JsonNode responseJson = objectMapper.readTree(response);
		String content = responseJson.get("data").get(0).get("content").asText();

		return content;
	}

	public String check() {
		try {
			String filePath = new File(basicDataFolder,"check.txt").toString();
			StringBuilder content = new StringBuilder();
			String line;
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			while (true) {
				if (!((line = reader.readLine()) != null)) break;
				content.append(line);
			}
			reader.close();
			return content.toString();
		} catch (IOException e) {
			return "无问答";
		}
	}

	public String readAndDeleteLine(String num) {
		String filepath = new File(basicDataFolder,"check.txt").toString();
		String result = "";
		int line_num = 0;

		try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
			String line;

			while ((line = br.readLine()) != null) {
				String[] parts = line.split(":");
				line_num++;
				if (parts[0].trim().equals("#" + num.trim())) {
					result = num.trim();
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (!result.isEmpty()) {
			deleteLine(filepath, line_num);
		}

		return result;
	}

	private void deleteLine(String filepath, int num) {
		try {
			// 读取文件内容
			List<String> lines = Files.readAllLines(Paths.get(filepath), Charset.defaultCharset());

			// 删除指定行
			lines.remove(num - 1);

			// 保存修改后的文件
			Files.write(Paths.get(filepath), lines, Charset.defaultCharset());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//删除行
	public void delete(String num) throws IOException {
		String excelFilePath = new File(basicDataFolder,"可爱词库.xlsx").toString();
		int rowNumberToDelete = Integer.parseInt(num);

		try {
			FileInputStream fis = new FileInputStream(excelFilePath);
			Workbook workbook = new XSSFWorkbook(fis);
			Sheet sheet = workbook.getSheetAt(0);

			int lastRowNum = sheet.getLastRowNum();

			if (rowNumberToDelete >= 0 && rowNumberToDelete <= lastRowNum) {
				sheet.shiftRows(rowNumberToDelete + 1, lastRowNum, -1);
				Row lastRow = sheet.getRow(lastRowNum);
				if (lastRow != null) {
					sheet.removeRow(lastRow);
				}
				FileOutputStream fos = new FileOutputStream(excelFilePath);
				workbook.write(fos);

				fos.close();

				System.out.println("Row deleted successfully.");
			} else {
				System.out.println("Invalid row number.");
			}
			workbook.close();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getCurrentDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date currentDate = new Date();
		return dateFormat.format(currentDate);
	}

	public String getUrlWord(String http) throws IOException {
		String encodedUrl = URLEncoder.encode(http, "UTF-8");
		encodedUrl = encodedUrl.replaceAll("%3A", ":").replaceAll("%2F", "/").replaceAll("%3F", "?").replaceAll("%3D", "=").replaceAll("%26", "&");
		URL url = new URL(encodedUrl);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");

		int responseCode = connection.getResponseCode();

		if (responseCode == HttpURLConnection.HTTP_OK) {
			InputStream inputStream = connection.getInputStream();

			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			StringBuilder response = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line).append("\n");
			}

			inputStream.close();

			return response.toString();
		} else {
			throw new IOException("HTTP request failed with response code: " + responseCode);
		}
	}

	public static String song(String songName) throws UnsupportedEncodingException {
		String apiURL = "https://api.yujn.cn/api/kugou.php?msg=" + URLEncoder.encode(songName.trim(), "UTF-8");
		try {
			URL url = new URL(apiURL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(CONNECTION_TIMEOUT);
			connection.setReadTimeout(READ_TIMEOUT);

			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
					StringBuilder response = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}

					Gson gson = new Gson();
					JsonObject jsonObject = gson.fromJson(response.toString(), JsonObject.class);

					if (jsonObject != null) {
						int code = jsonObject.get("code").getAsInt();
						if (code == 200) {
							JsonArray dataArray = jsonObject.get("data").getAsJsonArray();
							if (dataArray != null && dataArray.size() > 0) {
								JsonObject dataNode = dataArray.get(0).getAsJsonObject();
								String audioName = dataNode.get("audio_name").getAsString();
								String albumName = dataNode.get("album_name").getAsString();
								String imgURL = dataNode.get("img").getAsString();
								String authorName = dataNode.get("author_name").getAsString();
								String playURL = dataNode.get("play_url").getAsString();

								CompletableFuture.runAsync(() -> saveMusicFile(playURL), executor);

								return audioName + ";" + albumName + ";" + imgURL + ";" + authorName;
							} else {
								System.out.println("数据节点为空");
							}
						} else {
							System.out.println("请求失败，错误代码：" + code);
						}
					} else {
						System.out.println("解析 JSON 失败");
					}
				}
			} else {
				System.out.println("HTTP请求失败，错误代码：" + responseCode);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "false";
	}


	private static void saveMusicFile(String playURL) {
		try {
			URL fileUrl = new URL(playURL);
			HttpURLConnection fileConnection = (HttpURLConnection) fileUrl.openConnection();
			fileConnection.setRequestMethod("GET");
			fileConnection.setConnectTimeout(CONNECTION_TIMEOUT);
			fileConnection.setReadTimeout(READ_TIMEOUT);

			int fileResponseCode = fileConnection.getResponseCode();
			if (fileResponseCode == HttpURLConnection.HTTP_OK) {
				try (InputStream fileInputStream = fileConnection.getInputStream();
					 FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\z\\Desktop\\BOT素材\\song.amr")) {
					 byte[] buffer = new byte[BUFFER_SIZE];
					 int bytesRead;
					 while ((bytesRead = fileInputStream.read(buffer)) != -1) {
						fileOutputStream.write(buffer, 0, bytesRead);
					 }
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//星火类
	static class RoleContent {
		String role;
		String content;
	}

	static class JsonParse {
		Header header;
		Payload payload;
	}

	static class Header {
		int code;
		int status;
		String sid;
	}

	static class Payload {
		Choices choices;
	}

	static class Choices {
		List<Text> text;
	}

	static class Text {
		String content;
	}
}