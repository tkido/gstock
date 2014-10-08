Attribute VB_Name = "Module1"
Sub CopyCodes()
    Columns("BE:BE").Select
    ActiveWorkbook.Worksheets("RSS").Sort.SortFields.Clear
    ActiveWorkbook.Worksheets("RSS").Sort.SortFields.Add Key:=Range("BE1"), _
        SortOn:=xlSortOnValues, Order:=xlDescending, DataOption:=xlSortNormal
    With ActiveWorkbook.Worksheets("RSS").Sort
        .SetRange Range("A2:BM1000")
        .Header = xlNo
        .MatchCase = False
        .Orientation = xlTopToBottom
        .SortMethod = xlPinYin
        .Apply
    End With
    Columns("BB:BB").Select
    ActiveWorkbook.Worksheets("RSS").Sort.SortFields.Clear
    ActiveWorkbook.Worksheets("RSS").Sort.SortFields.Add Key:=Range("BB1"), _
        SortOn:=xlSortOnValues, Order:=xlAscending, DataOption:=xlSortNormal
    With ActiveWorkbook.Worksheets("RSS").Sort
        .SetRange Range("A2:BM1000")
        .Header = xlNo
        .MatchCase = False
        .Orientation = xlTopToBottom
        .SortMethod = xlPinYin
        .Apply
    End With

    Range("A1").EntireColumn.Copy
    Range("A1").Select
    
    Dim WSH
    Set WSH = CreateObject("Wscript.Shell")
    
    Dim url As String
    url = "http://tkido.com/jenkins/job/RSS/configure"
    WSH.Run url, 3
End Sub

Sub CopyLog()
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
        url = "https://www.sbisec.co.jp/ETGate"
        WSH.Run url, 3
        url = "https://kabu.click-sec.com/sec1-9/mypage/top.do"
        WSH.Run url, 3
        url = "http://www.traders.co.jp/index.asp"
        WSH.Run url, 3
        url = "http://stocks.finance.yahoo.co.jp/"
        WSH.Run url, 3
        url = "http://www.bloomberg.co.jp/markets/rates.html"
        WSH.Run url, 3
        url = "http://biz.yahoo.co.jp/ipo/"
        WSH.Run url, 3
        url = "https://twitter.com/monst_mixi"
        WSH.Run url, 3
        url = "http://www.google.co.jp/trends/explore#q=%E3%83%A2%E3%83%B3%E3%82%B9%E3%83%88%2C%20%E3%83%91%E3%82%BA%E3%83%89%E3%83%A9&date=today%2012-m&cmpt=q"
        WSH.Run url, 3
        url = "https://play.google.com/store/apps?hl=ja"
        WSH.Run url, 3
        url = "http://www.appannie.com/apps/google-play/top/taiwan/game/"
        WSH.Run url, 3
        url = "http://www.appannie.com/indexes/all-stores/rank/games/"
        WSH.Run url, 3
    Else
        code = Cells(ActiveCell.row, 1)
        Cells(ActiveCell.row, 1).Copy
        gmosec = Cells(1, 1)
        
        If re.Test(code) Then
            url = "http://karauri.net/" & code & "/"
            WSH.Run url, 3
            url = "https://www.google.co.jp/search?q=" & Cells(ActiveCell.row, 42)
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
            url = "http://profile.yahoo.co.jp/consolidate/" & code
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


