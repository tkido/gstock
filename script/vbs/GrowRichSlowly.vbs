'�}�[�P�b�g�X�s�[�h�̎������O�C���X�N���v�g

'���O�ɍs���Ă����ݒ�
'�}�[�P�b�g�X�s�[�h�Ń��O�C��ID�̓f�t�H���g�œ��͍ς݂ɂ��Ă���
'�}�[�P�b�g�X�s�[�h�ŃV���[�g�J�b�gF1�L�[�ɑ����T�}���[�����蓖�ĂĂ���

'�ݒ�t�@�C���̃C���N���[�h
Const ForReading = 1
    
Dim FileShell
Set FileShell = WScript.CreateObject("Scripting.FileSystemObject")

Function ReadFile(ByVal FileName)
    ReadFile = FileShell.OpenTextFile(FileName, ForReading, False).ReadAll()
End Function

Execute ReadFile(".\setting.vbs")

'�}�[�P�b�g�X�s�[�h�̋N��
Set WshShell = CreateObject("WScript.Shell")
WshShell.CurrentDirectory = MS_DIR
WshShell.Exec(MS_EXE)

'�}�[�P�b�g�X�s�[�h�̍ő剻
WshShell.CurrentDirectory = MS_DIR
Set objMS = WshShell.Exec(MS_EXE)
WshShell.AppActivate(objMS.ProcessID)
WScript.Sleep(1000)

'�}�[�P�b�g�X�s�[�h�̃A�N�e�B�u��
WshShell.AppActivate(MS_WINDOW_TITLE)

'�}�[�P�b�g�X�s�[�h�̎������O�C��
ret = WshShell.AppActivate(MS_WINDOW_TITLE)
WScript.Sleep(1000)

'�}�[�P�b�g�X�s�[�h�̃��O�C���y�[�W�̕\��
WshShell.SendKeys("{F1}") 
WScript.Sleep(1000)
WshShell.SendKeys("{ENTER}")
WScript.Sleep(1000)

'�}�[�P�b�g�X�s�[�h�̃p�X���[�h����
WshShell.SendKeys(MS_PASSWORD)
WScript.Sleep(1000)
WshShell.SendKeys("{ENTER}")
WScript.Sleep(1000)

'RSS�̋N��
Set WshShell = CreateObject("WScript.Shell")
WshShell.CurrentDirectory = MS_LAUNCHER_DIR
WshShell.Exec(MS_LAUNCHER_EXE)

'�G�N�Z�����J��
Dim objExcel
Set objExcel = CreateObject("Excel.Application")
objExcel.Visible = True
objExcel.Workbooks.Open RSS_FILE
Set objExcel = Nothing
