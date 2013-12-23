package com.suchangli.text;

import android.content.Context;
import android.text.Layout;
import android.text.Selection;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.Toast;

public class SelectedTextView extends EditText{
	private boolean DEBUG = true;
	private static String TAG = "MyTextView";

	public SelectedTextView(Context context){
		super(context);
		initialize();
	}
	public SelectedTextView(Context context, AttributeSet attrs){
		super(context, attrs);
		initialize();
	}
	private void initialize(){
		setGravity(Gravity.TOP);
	}

	
	@Override
	public boolean getDefaultEditable() {
		return false;
	}
 
	private long mStartTime = 0;
	private boolean mLongClick = false;
	private String mWord = null;
	private int[] mWordScop = new int[]{-1,-1};
	public boolean onTouchEvent(MotionEvent event) {
		 
		switch (event.getAction()){
			case MotionEvent.ACTION_DOWN:
				mStartTime = System.currentTimeMillis();
				break;
			case MotionEvent.ACTION_MOVE:
				if(mLongClick){
					String retWord = getTargetWord(event);
					wordPreviewWindow(retWord);
				}else{
					if(System.currentTimeMillis()-mStartTime > 500){
						mLongClick = true;
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				
				if(mLongClick){
					//��ʾ�ʵ�Ի���
					showDictDialog(mWord);
					mLongClick = false;
					Selection.removeSelection(getEditableText());
				}
				mStartTime = 0;
				break;
		}
		
		return true;
	}
	/**
	 * �������ʲ�ѯ�ĶԻ���
	 * �������������Ҫͨ���ʵ�ӿڽ��дʵ��ѯ
	 * @param word
	 */
	protected void showDictDialog(String word){
		if(!TextUtils.isEmpty(word)){
			Toast.makeText(getContext(), word, 1).show();
		}
		
	}
	/**
	 * ��ʾ���ʵ�Ԥ������������÷Ŵ󾵵�Ч��ȥ����
	 * ���������������ʵ��
	 * @param word
	 */
	protected void wordPreviewWindow(String word){
		if(DEBUG){
			Log.d(TAG, "����ĵ��ʣ�"+ word );
		}
	}
	//���ʽ����ַ����п��ܲ�ȫ����Ҫ��ӣ�Ϊ��ִ��Ч�ʿ��Բ�ASCCII��
	private static char[] sStopCharArray = new char[]{' ',',','.',';','?','!','(',')'};
	//��鵥���Ƿ��������������true�����򷵻�false,�������ּ��ķ�ʽЧ�ʲ��ߣ��������������ط������Ż�
	private boolean checkStoped(char c){
		for(char cr : sStopCharArray){
			if(cr-c==0){
				return true;
			}
		}
		
		return false;
	} 
	/**
	 * �����������Ϊ��ȷ�����������һ������
	 * ���⣺����ˢ��android4.0�ϣ��޷���ʾѡ��Ч�����Ժ��������������ֻ�������������ֻ��޷���ʾ�������
	 * ��һ�ַ�ʽ���д���ѡ�������
	 * @param event
	 * @return ����ĵ��ʣ����Ϊ�վͷ��ؿ��ַ���
	 */
	private String getTargetWord(MotionEvent event){
		
		
		String text = getText().toString();
		
		Layout layout = getLayout();
		//��õ������
		int lineNum = layout.getLineForVertical(getScrollY() + (int) event.getY());
		//��õ�����ַ�λ�ã����������һ�е�λ��
		int lineoff = layout.getOffsetForHorizontal(lineNum,(int) event.getX());
		//��������ַ�����Ͳ��ü����ˣ�ֱ�ӷ��ؿ��ַ���
		if(lineoff == text.length()){
			Selection.removeSelection(getEditableText());
			if(DEBUG){
				Log.d(TAG, "������кţ�" + lineNum + ",��������е��ַ�λ��:" + lineoff);
			}
			mWord = "";
			return mWord;
		}
		//�����ͬһ�����ʣ���ֹ�ظ�����
		if(mLongClick&&mWordScop[0] <= lineoff && lineoff < mWordScop[1]){
			
			return mWord;
		}
		
		
		Selection.removeSelection(getEditableText());
		if(DEBUG){
			Log.d(TAG, "������кţ�" + lineNum + ",��������е��ַ�λ��:" + lineoff);
			Log.d(TAG, "��Ӧ���ַ�:"+text.charAt(lineoff));
			if(lineoff>1||text.length()>lineoff-1){
				Log.d(TAG, "��Ӧ���ַ�:"+text.charAt(lineoff-1)+text.charAt(lineoff)+text.charAt(lineoff+1));
			}
		}
		 
		//��õ�������������һ���ַ���λ��,�����ַ���λ��
		int lineEndCharPostion = layout.getLineEnd(lineNum);
		//��õ���������е�һ���ַ���λ�ã������ַ���λ��
		int lineStartCharPostion = layout.getLineStart(lineNum);
		
		if(DEBUG){
			Log.d(TAG, "lineEndCharPostion��" + lineEndCharPostion + ",lineStartCharPostion:" + lineStartCharPostion);
		}
		
		//�������ĵ��ʵ���textview�е�λ��
		int clikeCharPostion = lineoff;
		
		int wordStartPosition = -1;
		
		if(DEBUG){
			
			Log.d(TAG,"clikeCharPostion:"+clikeCharPostion+ ",lineEndCharPostion��" + lineEndCharPostion + ",lineStartCharPostion:" + lineStartCharPostion);
		}
		
		if(checkStoped(text.charAt(clikeCharPostion))){
			return "";
		}
		//���ƶ�
		for(int i = clikeCharPostion; i >= 0; i--){
			if(i==0){
				wordStartPosition = i;
			}
			
			char c = text.charAt(i);
			if(checkStoped(c)){
				wordStartPosition = i+1;
				break;
			}
			
		}
		//���ƶ�
		int wordEndPosition = -1;
		for(int i = clikeCharPostion; i < text.length(); i++){
			if(i == text.length()-1){
				wordEndPosition = i+1;
			}
			char c = text.charAt(i);
			if(checkStoped(c)){
				wordEndPosition = i;
				break;
			}
		}
		if(DEBUG){
			Log.d(TAG, "wordStartPosition��" + wordStartPosition + ",wordEndPosition:" + wordEndPosition);
		}
		//word
		if(wordStartPosition<0 || wordEndPosition > text.length()){
			return "";
		}
		String word = text.substring(wordStartPosition, wordEndPosition);
		mWord = word;
		
		
		if(DEBUG){
			Log.d(TAG, "word��" + mWord);
		}
		
		//���浥�ʵķ�Χ
		mWordScop[0] = wordStartPosition;
		mWordScop[1] = wordEndPosition;
		
		if(wordStartPosition > 0 && wordEndPosition > 0){
			Selection.setSelection(getEditableText(), wordStartPosition, wordEndPosition);
		}
		
		return word;
	}
	/**
	 * ���תȫ���ַ�������������˴���Ӣ���Ű治�ÿ��ģ����ǻ������ڴ棨�����ռһ���ֽڣ�ȫ��ռ�����Լ���
	 * @param input
	 * @return
	 */
	public static String toDBC(String input) {
		   char[] c = input.toCharArray();
		   for (int i = 0; i< c.length; i++) {
		       if (c[i] == 12288) {
		         c[i] = (char) 32;
		         continue;
		       }if (c[i]> 65280&& c[i]< 65375)
		          c[i] = (char) (c[i] - 65248);
		       }
		   return new String(c);
	}
}
