'�}�[�P�b�g�X�s�[�h�̎������O�C���X�N���v�g

'���O�ɍs���Ă����ݒ�
'�}�[�P�b�g�X�s�[�h�Ń��O�C��ID�̓f�t�H���g�œ��͍ς݂ɂ��Ă���
'�}�[�P�b�g�X�s�[�h�ŃV���[�g�J�b�gF1�L�[�ɑ����T�}���[�����蓖�ĂĂ���

'Dvoraker�̒�~
'WMI�ɂĎg�p����e��I�u�W�F�N�g���`�E��������B
Dim oClassSet
Dim oClass
Dim oLocator
Dim oService
Dim sMesStr

'���[�J���R���s���[�^�ɐڑ�����B
Set oLocator = WScript.CreateObject("WbemScripting.SWbemLocator")
Set oService = oLocator.ConnectServer
'�N�G���[������WQL�ɂĎw�肷��B
Set oClassSet = oService.ExecQuery("Select * From Win32_Process Where Description=""dvoraker.exe""")
'�R���N�V��������͂���B
For Each oClass In oClassSet
oClass.Terminate
Next

'�g�p�����e��I�u�W�F�N�g����Еt������B
Set oClassSet = Nothing
Set oClass = Nothing
Set oService = Nothing
Set oLocator = Nothing

'�}�[�P�b�g�X�s�[�h�̋N��
Set WshShell = CreateObject("WScript.Shell")
WshShell.CurrentDirectory = "D:\Program Files (x86)\MarketSpeed\MarketSpeed"
WshShell.Exec("D:\Program Files (x86)\MarketSpeed\MarketSpeed\MarketSpeed.exe")

'�}�[�P�b�g�X�s�[�h�̍ő剻
WshShell.CurrentDirectory = "D:\Program Files (x86)\MarketSpeed\MarketSpeed"
Set objMS = WshShell.Exec("D:\Program Files (x86)\MarketSpeed\MarketSpeed\MarketSpeed.exe")
WshShell.AppActivate(objMS.ProcessID)
WScript.Sleep(1000)
'WshShell.SendKeys("% X")
'WScript.Sleep(1000)

'�}�[�P�b�g�X�s�[�h�̃A�N�e�B�u��
WshShell.AppActivate("Market Speed Ver11.21")

'�}�[�P�b�g�X�s�[�h�̎������O�C��
MS_WINDOW_TITLE = "Market Speed Ver11.21"
MS_WINDOW_LOGIN_TITLE = "Market Speed - ۸޲�"

ret = WshShell.AppActivate(MS_WINDOW_TITLE)
WScript.Sleep(1000)

'�}�[�P�b�g�X�s�[�h�̃��O�C���y�[�W�̕\��
WshShell.SendKeys("{F1}") 
WScript.Sleep(1000)
WshShell.SendKeys("{ENTER}")
WScript.Sleep(1000)

'�}�[�P�b�g�X�s�[�h�̃p�X���[�h����
WshShell.SendKeys("CZPW7960")
WScript.Sleep(1000)
WshShell.SendKeys("{ENTER}")
WScript.Sleep(1000)

'RSS�̋N��
Set WshShell = CreateObject("WScript.Shell")
WshShell.CurrentDirectory = "D:\Program Files (x86)\MarketSpeed\MLauncher"
WshShell.Exec("D:\Program Files (x86)\MarketSpeed\MLauncher\MLauncher.exe RSS")

'Dvoraker�̍ċN��
Set WshShell = CreateObject("WScript.Shell")
WshShell.Exec("C:\OLS\Dvoraker\dvoraker.exe -p -k -2")

'�G�N�Z�����J��
Dim objExcel
Set objExcel = CreateObject("Excel.Application")
objExcel.Visible = True
objExcel.Workbooks.Open "C:\Users\tkido\Dropbox\xls\rss.xlsm"
Set objExcel = Nothing