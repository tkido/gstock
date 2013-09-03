Attribute VB_Name = "Module1"
Sub CopyLog()
Attribute CopyLog.VB_ProcData.VB_Invoke_Func = "E\n14"
    Dim name As String
    name = Format(Date, "mmdd")
    
    Worksheets("RSS").Copy After:=Worksheets("RSS")
    Worksheets("RSS (2)").name = name
    
    Worksheets(name).UsedRange.Copy
    Worksheets(name).UsedRange.PasteSpecial Paste:=xlPasteValues
    Worksheets(name).Range("A1").Select
    Application.CutCopyMode = False
    
    Worksheets("RSS").Activate
End Sub

Sub OpenHtml()
Attribute OpenHtml.VB_ProcData.VB_Invoke_Func = "e\n14"
    Dim code As String
    Dim url As String
    
    Dim WSH
    Set WSH = CreateObject("Wscript.Shell")
    
    Dim re As RegExp
    Set re = New RegExp
    re.Pattern = "[0-9]{4}"
    
    If ActiveCell.row = 1 Then
        url = "https://kabu.click-sec.com/sec1-9/mypage/top.do"
        WSH.Run url, 3
        url = "http://www.traders.co.jp/index.asp"
        WSH.Run url, 3
        url = "http://www.bloomberg.co.jp/markets/rates.html"
        WSH.Run url, 3
    Else
        code = Cells(ActiveCell.row, 1)
        gmosec = Cells(1, 1)
        
        If re.Test(code) Then
            url = "http://kabu-sokuhou.com/brand/item/code___" & code & "/"
            WSH.Run url, 3
            url = "https://www.google.co.jp/search?q=" & Cells(ActiveCell.row, 35)
            WSH.Run url, 3
            url = "https://www.google.co.jp/search?q=" & Cells(ActiveCell.row, 2)
            WSH.Run url, 3
            url = "https://kabu.click-sec.com/sec1-" & gmosec & "/kabu/meigaraInfo.do?securityCode=" & code
            WSH.Run url, 3
            url = "http://www.nikkei.com/markets/company/kigyo/kigyo.aspx?scode=" & code
            WSH.Run url, 3
            url = "http://info.finance.yahoo.co.jp/stockholder/detail/?code=" & code
            WSH.Run url, 3
            url = "http://www.nikkei.com/markets/company/kessan/shihyo.aspx?scode=" & code
            WSH.Run url, 3
            url = "http://tkido.com/stock/" & code & ".html"
            WSH.Run url, 3
        Else
            url = "http://finance.yahoo.com/q?s=" & code
            WSH.Run url, 3
            url = "https://www.google.com/#output=search&q=" & URLEncode(Cells(ActiveCell.row, 2))
            WSH.Run url, 3
        End If
    End If
    Set WSH = Nothing
End Sub

Public Function URLEncode(value As String) As String
    Set sc = CreateObject("ScriptControl")
    sc.Language = "JavaScript"
    Set js = sc.CodeObject
    URLEncode = js.encodeURIComponent(value)
End Function