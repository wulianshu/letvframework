概述：

基于开源项目搭建的框架

实现基础逻辑，与具体业务无关

向上提供网络请求、图片加载、下拉刷新、下载管理、工具类、UI控件（待补充）等功能

封装了letv_http_api，继承BaseRequest和BaseParser写具体的请求

BaseActivity和BaseFragment里有sendRequest方法用来发请求，拿不到Context的地方可以用取CommonUtil的单例对象，然后再发请求

增加分享库，支持分享到微博、微信、QQ

------------------------------------------------------------------

采用的开源项目有：

1、volley
民间高手修复官方bug版
（https://github.com/mcxiaoke/android-volley）

2、glide
可以在manifest中配置使用volley，如有必要，可以改glide代码默认用volley发请求

3、ptr-lib
封装了下拉ListView、GridView、RecyclerView，借鉴SuperRecyclerView做了空页面提示
鉴于SuperRecyclerView做的比较简单，扩展性有限，暂不使用
（https://github.com/desmond1121/Android-Ptr-Comparison）

4、GreenDao
数据库文件生成需要具体应用自己去做，基础库只提供了一个GreenDao数据库升级的辅助类

-------------------------------------------------------------------

建议：

1、继承基础类，BaseActivity、BaseFragment、BaseApplication、MyBaseAdapter等

2、个性化的逻辑实现在子类，减少对基类的修改

3、杜绝匿名类方式使用Handler，自定义静态内部类，避免内存泄漏（参考BaseActivity）

4、使用了WebView的界面，destroy时主动调用UiUtil.destroyWebView，释放内存

5、及时提交svn、git，合并代码，每天提供可用版本（敏捷开发）

6、待续。。。
