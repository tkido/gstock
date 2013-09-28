'マーケットスピードの自動ログインスクリプト

'事前に行っておく設定
'マーケットスピードでログインIDはデフォルトで入力済みにしておく
'マーケットスピードでショートカットF1キーに総合サマリーを割り当てておく

'Dvorakerの停止
'WMIにて使用する各種オブジェクトを定義・生成する。
Dim oClassSet
Dim oClass
Dim oLocator
Dim oService
Dim sMesStr

'ローカルコンピュータに接続する。
Set oLocator = WScript.CreateObject("WbemScripting.SWbemLocator")
Set oService = oLocator.ConnectServer
'クエリー条件をWQLにて指定する。
Set oClassSet = oService.ExecQuery("Select * From Win32_Process Where Description=""dvoraker.exe""")
'コレクションを解析する。
For Each oClass In oClassSet
oClass.Terminate
Next

'使用した各種オブジェクトを後片付けする。
Set oClassSet = Nothing
Set oClass = Nothing
Set oService = Nothing
Set oLocator = Nothing

'マーケットスピードの起動
Set WshShell = CreateObject("WScript.Shell")
WshShell.CurrentDirectory = "D:\Program Files (x86)\MarketSpeed\MarketSpeed"
WshShell.Exec("D:\Program Files (x86)\MarketSpeed\MarketSpeed\MarketSpeed.exe")

'マーケットスピードの最大化
WshShell.CurrentDirectory = "D:\Program Files (x86)\MarketSpeed\MarketSpeed"
Set objMS = WshShell.Exec("D:\Program Files (x86)\MarketSpeed\MarketSpeed\MarketSpeed.exe")
WshShell.AppActivate(objMS.ProcessID)
WScript.Sleep(1000)
'WshShell.SendKeys("% X")
'WScript.Sleep(1000)

'マーケットスピードのアクティブ化
WshShell.AppActivate("Market Speed Ver11.21")

'マーケットスピードの自動ログイン
MS_WINDOW_TITLE = "Market Speed Ver11.21"
MS_WINDOW_LOGIN_TITLE = "Market Speed - ﾛｸﾞｲﾝ"

ret = WshShell.AppActivate(MS_WINDOW_TITLE)
WScript.Sleep(1000)

'マーケットスピードのログインページの表示
WshShell.SendKeys("{F1}") 
WScript.Sleep(1000)
WshShell.SendKeys("{ENTER}")
WScript.Sleep(1000)

'マーケットスピードのパスワード入力
WshShell.SendKeys("CZPW7960")
WScript.Sleep(1000)
WshShell.SendKeys("{ENTER}")
WScript.Sleep(1000)

'RSSの起動
Set WshShell = CreateObject("WScript.Shell")
WshShell.CurrentDirectory = "D:\Program Files (x86)\MarketSpeed\MLauncher"
WshShell.Exec("D:\Program Files (x86)\MarketSpeed\MLauncher\MLauncher.exe RSS")

'Dvorakerの再起動
Set WshShell = CreateObject("WScript.Shell")
WshShell.Exec("C:\OLS\Dvoraker\dvoraker.exe -p -k -2")

'エクセルを開く
Dim objExcel
Set objExcel = CreateObject("Excel.Application")
objExcel.Visible = True
objExcel.Workbooks.Open "C:\Users\tkido\Dropbox\xls\rss.xlsm"
Set objExcel = Nothing