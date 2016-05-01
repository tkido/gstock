'マーケットスピードの自動ログインスクリプト

'事前に行っておく設定
'マーケットスピードでログインIDはデフォルトで入力済みにしておく
'マーケットスピードでショートカットF1キーに総合サマリーを割り当てておく

'設定ファイルのインクルード
Const ForReading = 1
    
Dim FileShell
Set FileShell = WScript.CreateObject("Scripting.FileSystemObject")

Function ReadFile(ByVal FileName)
    ReadFile = FileShell.OpenTextFile(FileName, ForReading, False).ReadAll()
End Function

Execute ReadFile(".\setting.vbs")

'マーケットスピードの起動
Set WshShell = CreateObject("WScript.Shell")
WshShell.CurrentDirectory = MS_DIR
WshShell.Exec(MS_EXE)

'マーケットスピードの最大化
WshShell.CurrentDirectory = MS_DIR
Set objMS = WshShell.Exec(MS_EXE)
WshShell.AppActivate(objMS.ProcessID)
WScript.Sleep(1000)

'マーケットスピードのアクティブ化
WshShell.AppActivate(MS_WINDOW_TITLE)

'マーケットスピードの自動ログイン
ret = WshShell.AppActivate(MS_WINDOW_TITLE)
WScript.Sleep(1000)

'マーケットスピードのログインページの表示
WshShell.SendKeys("{F1}") 
WScript.Sleep(1000)
WshShell.SendKeys("{ENTER}")
WScript.Sleep(1000)

'マーケットスピードのパスワード入力
WshShell.SendKeys(MS_PASSWORD)
WScript.Sleep(1000)
WshShell.SendKeys("{ENTER}")
WScript.Sleep(1000)

'RSSの起動
Set WshShell = CreateObject("WScript.Shell")
WshShell.CurrentDirectory = MS_LAUNCHER_DIR
WshShell.Exec(MS_LAUNCHER_EXE)

'エクセルを開く
Dim objExcel
Set objExcel = CreateObject("Excel.Application")
objExcel.Visible = True
objExcel.Workbooks.Open RSS_FILE
Set objExcel = Nothing
