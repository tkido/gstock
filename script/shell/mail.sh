/usr/sbin/sendmail takanorikido@gmail.com <<_EOT_
From: jenkins@tkido.com
To: takanorikido@gmail.com
Subject: ${JOB_NAME}-${BUILD_NUMBER} completed!!

Download URL:
${BUILD_URL}artifact/data/rss/result.txt
${BUILD_URL}artifact/data/rss/downloaded.txt
_EOT_
