package com.example.accesscontacts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
  
	public static final View title = null;
	Button add,show;
	ListView result;
	TextView name,num,personname,personId,personnum;
	ContentResolver resolver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		add=(Button) this.findViewById(R.id.add);
		show=(Button) this.findViewById(R.id.show);
		MyOnClickListener myOnClickListener =new MyOnClickListener ();
		add.setOnClickListener(myOnClickListener);
		show.setOnClickListener(myOnClickListener);
	}
       
	public void addPerson(){
		
		String nameStr=name.getText().toString();
		String numStr=num.getText().toString();
		ContentValues values=new ContentValues();

		Uri rawContactUri=resolver.insert(RawContacts.CONTENT_URI,values);
		long contactId=ContentUris.parseId(rawContactUri);
		values.clear();
		values.put(Data.RAW_CONTACT_ID, contactId);
		values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
		values.put(StructuredName.GIVEN_NAME, nameStr);
		resolver.insert(android.provider.ContactsContract.Data.CONTENT_URI, values);
		values.clear();
		values.put(Data.RAW_CONTACT_ID,contactId);
		values.put(Data.MIMETYPE,Phone.CONTENT_ITEM_TYPE);
		values.put(Phone.NUMBER, numStr);
		values.put(Phone.TYPE, Phone.TYPE_MOBILE);
		resolver.insert(android.provider.ContactsContract.Data.CONTENT_URI, values);
		Toast.makeText(MainActivity.this, "联系人信息添加成功", 1000).show();
		
	}
	
 public ArrayList<Map<String,String>>queryPerson(){
	  ArrayList<Map<String,String>>detail=new ArrayList<Map<String,String>>();
	  
	Cursor cursor = resolver.query(
		        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
		        null, null);
	while (cursor.moveToNext()) {
		Map<String,String>person=new HashMap<String,String>();
		 String personId = cursor.getString(cursor
				    .getColumnIndex(ContactsContract.Contacts._ID));
	    // 获取联系人姓名
	    String name = cursor.getString(cursor
	    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
	    person.put("id",personId);
	    person.put("name",name);
	    // 获取联系人手机号
	    Cursor nums=resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI,
	    		null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"="+personId, null,null);
	    if(nums.moveToNext()){
	    String num = nums.getString(nums.getColumnIndex
	    		(ContactsContract.CommonDataKinds.Phone.NUMBER));
	    person.put("num", num);
	    }
	    nums.close();
	    detail.add(person);
	}
	cursor.close();
	return detail;
	

  }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
 private class MyOnClickListener  implements OnClickListener{

	@Override
	public void onClick(View v) {
	switch(v.getId()){
	case R.id.add:
		addPerson();
		break;
	case R.id.show:
		title.setVisibility(View.VISIBLE);
	ArrayList<Map<String,String>>persons=queryPerson();
		SimpleAdapter adapter=new SimpleAdapter(MainActivity.this,persons,R.layout.activity_main,
			new String[]{"id","name","num"},new int[]{R.id.personId,R.id.personname,R.id.personnum});
	result.setAdapter(adapter);
		break;
		default:
			break;
	}
		
	} 
 }
    
}
