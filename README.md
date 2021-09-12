## 效果图
![](https://raw.githubusercontents.com/shenzhen2017/resource/main/2021/september/p4.gif)
> 人有悲欢离合，月有阴晴圆缺，此事古难全，

> 但愿人长久，千里共婵娟。

> 恰逢中秋佳节，我们今天就使用`Compose`来实现一下月相变化动画吧~

> 感兴趣的同学可以点个`Star` : [Compose 实现月亮阴晴圆缺动画](https://github.com/shenzhen2017/ComposeMoon)

## 主要思路
### 满天繁星
为了实现月相动画，我们首先需要一个背景，因此我们需要一个好看的星空，最好还有闪烁的效果
为为实现星空背景，我们需要做以下几件事
1. 绘制背景
2. 生成几十个星星，在背景上随机分布
3. 通过`scale`与`alpha`动画，实现每个星星的闪烁效果

我们一起来看下代码
```kotlin
@Composable
fun Stars(starNum: Int) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val list = remember { mutableStateListOf<Star>() }
        LaunchedEffect(true) {
            for (i in 0..starNum) {
                delay(100L)
                //添加星星，它们的位置在屏幕上随机
                list.add(Star(maxWidth.value * density, maxHeight.value * density))
            }
        }
        list.forEach {
            Star(it)
        }
    }
}

@Composable
fun Star(star: Star) {
    var progress: Float by remember { mutableStateOf(0f) }
    val infiniteTransition = rememberInfiniteTransition()
    ....
    star.updateStar(progress) // 通过动画更新progress,从而更新star的属性值
    Canvas(modifier = Modifier.wrapContentSize()) {
        scale(star.scale, Offset(star.x, star.y)) { // 缩放动画
            drawCircle(
                star.starColor,
                star.radius,
                center = Offset(star.x, star.y),
                alpha = star.alpha // alpha动画
            )
        }
    }
}
```

### 月相变化
月相，天文学术语。（`phase of the moon`）是天文学中对于地球上看到的月球被太阳照明部分的称呼。随着月亮每天在星空中自东向西移动一大段距离，它的形状也在不断地变化着，这就是月亮位相变化，叫做月相。
它的变化过程如下图所示

![](https://raw.githubusercontents.com/shenzhen2017/resource/main/2021/september/p5.jpeg)

每个阶段都有各自的名字，如下图所示：

![](https://raw.githubusercontents.com/shenzhen2017/resource/main/2021/september/p6.jpeg)

可以看出，月相变化过程还是有些复杂的，那我们怎么实现这个效果呢？

#### 思路分析
为了实现月相变化,首先我们需要画一个圆，代表月亮，最终的满月其实就是这样，比较简单

有了满月，如何在它的基础上，画出其它的月相呢？我们可以通过图像混合模式来实现

图像混合模式定义的是，当两个图像合成时，图像最终的展示方式。在`Androd`中，有相应的`API`接口来支持图像混合模式，即`Xfermode`.

图像混合模式主要有以下16种,以下这张图片从一定程度上形象地说明了图像混合的作用，两个图形一圆一方通过一定的计算产生不同的组合效果，具体如下

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/2ccb418c0dc8431fb449cb5ddd117bd5~tplv-k3u1fbpfcp-watermark.awebp)

我们为了实现月相动画，主要需要使用以下两种混合模式
- `DST_OUT`:只在源图像和目标图像不相交的地方绘制【目标图像】，在相交的地方根据源图像的`alpha`进行过滤，源图像完全不透明则完全过滤，完全透明则不过滤
- `DST_OVER`:将目标图像放在源图像上方

我们已经了解了图形混合模式，那么需要在满月上画什么才能实现其它效果呢?
我们可以通过在满月上放一个半圆`+`一个椭圆来实现
![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/f872b4e4c2e6417bbd46c8547deb007a~tplv-k3u1fbpfcp-watermark.awebp)
1. 如上所示，椭圆上水平的线叫长轴，竖直的线叫短轴
2. 短轴不变，长轴半径从0到满月半径发生变化，再加上一个半圆，就可以实现不同的月相
3. 比如为了画上蛾眉月，可以通过左半边画半圆，再加上一个椭圆，两都都使用`DST_OVER`混合模式来实现，就实现了它们两的并集，然后覆盖在下层满月上，就实现了上蛾眉月
4. 为了画渐盈凸月,则同样就左半边以`DST_OVER`画半圆,再以`DST_OUT`画椭圆，就只剩下半圆与椭圆不相交的部分，再与下层的满月混合，就实现了渐盈凸月

这样说可能还是比较抽象，感兴趣的同学可下载源码详细了解下

#### 源码实现
```kotlin
//月亮动画控件
@Composable
fun Moon(modifier: Modifier) {
    var progress: Float by remember { mutableStateOf(0f) }
    BoxWithConstraints(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .size(canvasSize)
                .align(Alignment.TopCenter)
        ) {
            drawMoonCircle(this, progress)
            drawIntoCanvas {
                it.withSaveLayer(Rect(0f, 0f, size.width, size.height), paint = Paint()) {
                    if (progress != 1f) {
                    	//必须先画半圆，再画椭圆
                        drawMoonArc(this, it, paint, progress)
                        drawMoonOval(this, it, paint, progress)
                    }
                }
            }
        }
    }
}

// 1.首先画一个满月
private fun drawMoonCircle(scope: DrawScope, progress: Float) {
	//....
    drawCircle(Color(0xfff9dc60))
}

// 2. 画半圆
private fun drawMoonArc(scope: DrawScope, canvas: Canvas, paint: Paint, progress: Float) {
    val sweepAngle = when { //从新月到满月在一边画半圆，从满月回到新月则在另一边画半圆
        progress <= 0.5f -> 180f
        progress <= 1f -> 180f
        progress <= 1.5f -> -180f
        else -> -180f
    }
    paint.blendMode = BlendMode.DstOver //半圆的混合模式始终是DstOver
    scope.run {
        canvas.drawArc(Rect(0f, 0f, size.width, size.height), 90f, sweepAngle, false, paint)
    }
}

// 2. 画椭圆
private fun drawMoonOval(scope: DrawScope, canvas: Canvas, paint: Paint, progress: Float) {
    val blendMode = when { //椭圆的混合模式会发生变化，这里需要注意下
        progress <= 0.5f -> BlendMode.DstOver
        progress <= 1f -> BlendMode.DstOut
        progress <= 1.5f -> BlendMode.DstOut
        else -> BlendMode.DstOver
    }
    paint.blendMode = blendMode
    scope.run {
        canvas.drawOval(
            Rect(offset = topLeft, size = Size(horizontalAxis, verticalAxis)), //椭圆的长轴会随着动画变化
            paint = paint
        )
    }
}
```
如上所示:
1. 主要就是3个步骤，画满月，再画半圆，再画椭圆
2. 半圆的混合模式始终是`DstOver`,而椭圆的混合模式会发生变化，它们的颜色都是黑色。
3. 可以看到半圆与椭圆新建了一个`Layer`，混合模式的变化，表示的就是最后剩下的是它们的并集，还是`Dst`不相交的部分，最后覆盖到满月上，所以必须先画半圆
4. 随着动画的变化，椭圆的长轴会发生变化，这样就可以实现不同的月相

### 诗歌打字机效果
上面其实已经做得差不多了，我们最后再添加一些诗歌，并为它们添加打字机效果
```kotlin
@Composable
fun PoetryColumn(
    list: List<Char>,
    offsetX: Float = 0f,
    offsetY: Float = 0f
) {
    val targetList = remember { mutableStateListOf<Char>() }
    LaunchedEffect(list) {
        targetList.clear()
        list.forEach {
            delay(500) //通过在LaunchedEffect中delay实现动画效果
            targetList.add(it)
        }
    }
    //将 Jetpack Compose 环境的 Paint 对象转换为原生的 Paint 对象
    val textPaint = Paint().asFrameworkPaint().apply {
        //...
    }
    Canvas(modifier = Modifier.wrapContentSize()) {
        drawIntoCanvas {
            for (i in targetList.indices) {
                it.nativeCanvas.drawText(list[i].toString(), x, y, textPaint)
                y += delta // 更新文字y轴位置
            }
        }
    }
}
```
如上所示，代码比较简单
1. 通过在`LaunchedEffect`中调用挂起函数，来实现动画效果
2. 为了实现竖直方向的文字，我们需要使用`Paint`来绘制`Text`，而不能使用`Text`组件
3. `Compose`目前还不支持直接绘制`Text`，所以我们需要调用`asFrameworkPaint`将其转化为原生的`Paint`

## 总结
通过以上步骤，我们就通过`Compose`实现了月相阴晴圆缺+星空闪耀+诗歌打字机的动画效果
开发起来跟`Android`自定义绘制其实并没有多大差别，代码量因为`Compose`强大的`API`与声明式特点可能还有所减少
在我看来，`Compose`已经相当成熟了，而且将是`Android UI`的未来~

开源不易，如果项目对你有所帮助，欢迎点赞,`Star`,收藏~
### 参考资料
[蹭中秋热度来了~Android 自定义View——月有阴晴圆缺](https://juejin.cn/post/7006142194230755341)

[「寒草的中秋献礼🥮，实现30s前端创意动画」陪你看日落和月升｜与你赏星空和诗歌](https://juejin.cn/post/7005355142413287438)

### 项目地址
[Compose 实现月亮阴晴圆缺动画](https://github.com/shenzhen2017/ComposeMoon)
