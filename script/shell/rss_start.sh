export JAVA_OPTS='-Dfile.encoding=UTF-8'
echo "${TABLE}" > /home/jenkins/gstock/data/rss/table.txt
echo "${CONFIG}" > /home/jenkins/gstock/data/rss/conf.properties

/usr/local/share/scala-2.9.2/bin/scala com.tkido.stock.rss.main