export JAVA_OPTS='-Dfile.encoding=UTF-8'
echo "${TABLE}" > data/table.txt
echo "${CONFIG}" > data/conf.properties

/usr/local/share/scala-2.9.2/bin/scala com.tkido.stock.main