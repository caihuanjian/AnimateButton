# Applist维护文档
---------
## 概要
applist主要做了以下几个工作：

- 启动的时候去读取系统已安装的应用（那些含activity的action为android.intent.action.MAIN，并且category为android.intent.category.LAUNCHER的应用）
- 数据库存储已安装的应用信息（主要为了保存位置信息），每次启动Applist都会去检查是否有应用卸载或添加，然后更新数据库
- 对最终得到的一个应用列表进行排序并显示
- activity注册应用卸载、安装、disable...等等回调，及时更新界面
- 通过contentprovider提供快捷应用数据给launcher展示

## 主要的类
### pageapp包下的

- AppModel:负责应用读取、数据库数据更新，监听应用卸载、添加等广播
- AllAppsList：维护一个应用列表，即时更新
- ApplistDataBaseOperation：只负责应用列表的数据库（显示在桌面的几个应用由另一个数据库进行存储）
- HomeAppDBManager：数据库工具类，操作launcher上的那个几个应用
- WorkSpaceAppLoder：第一次安装时读取默认配置的桌面应用
- AppJsonManager:解析对系统应用自行配置的icon（奇葩需求，为何不让应用自行更换icon）、背景

## MainActivity中的几个回调方法
|方法|说明|
| --- | --- |
| bindAllApplications| 开机后第一次启动applist，加载完App后回调 |
| onlyBindAllApps| 开机后启动applist，并且加载过了app，进程没被杀死，当前还保存着数据，不会再去加载，直接回调该方法 |
| bindAppsAdded| 新安装了应用或者应用从disable状态变成enable状态，或者插入了U盘，U盘安装有应用，回调该方法 |
| bindAppsUpdated| 覆盖安装应用时回调 |
| bindAppsRemoved| 卸载应用等...与bindAppsAdded相反 |

## 上下左右调整应用位置----GridAnimationScrollView
### 初始化，渲染view
GridAnimationScrollView 内部由GridLayout来摆放AppView，setUpGrid方法中做了如何摆放App的逻辑。
需要注意几点：

- 需要根据app数量计算出几行几列
- 因为需求是按首字母排序，setUpGrid方法拿到的已经是排好序的列表，这里要做的就是依次取出来从左往右一行一行排
- 需要计算什么时候该另起一行！！！

### 移动view位置
### 左右交换
1、进入点击某个view进入交换状态时，在点击的位置生成一个镜像，将原来的view隐藏掉，即透明度设成0
2、先交换两个Appview的数据信息，即AppIcon对象，并且更新view。
整个过程如下：

交换前：            镜像A（被隐藏的A）  <--->   B
交换数据更新view：          镜像A（被隐藏的B）  <--->   A
设置透明度：镜像A（B显示了） <----> A看不见了
动画：对B和镜像A进行动画（B从右边的位置移动到左边）

### 跨行交换
整体的思路跟左右交换的一样，只不过需要交换数据的view变多了。思路是找出需要变化的view的索引，然后交换数据更新view，为每个view生成动画，跨行肯定有一个需要做跨行的动画，其它全部都是向左或向右的动画，
calculateAffectedPos(int dragPosition, int newPosition)会计算出受影响的这些索引
比如1与5交换位置，受影响的就是1~5：
交换前
0 1 2 3
4 5 6 7
8 9 
交换数据并更新view，动画思想与左右交换一直：
0 2 3 4
5 1 6 7
8 9

>调整位置之后，并不会立马去更新数据库的位置信息，而是在activity的ondestroy里去更新,改成onstop更新会好点