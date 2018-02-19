运行程序前请先将InputFiles.zip解压缩到该文件所在的目录。

运行程序的示例命令：

java -Xms96m -Xmx128m -XX:NewSize=64m -XX:SurvivorRatio=32 -Dx.stat.task=io.github.viscent.mtia.ch4.case02.MultithreadedStatTask -Dx.input.buffer=8192 -Dx.block.size=2000 io.github.viscent.mtia.ch4.case02.CaseRunner4_2
