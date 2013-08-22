package es.urjc.escet.gsyc.html.internal;

public class Html extends HtmlElement {
	
	private HtmlElement title;
	private HtmlElement head;
	private HtmlElement body;
	
	public Html(){
		super(HTML_TAG);
		title = new HtmlElement(TITLE_TAG);
		head = new HtmlElement(HEAD_TAG);
		body = new HtmlElement(BODY_TAG);
		
		head.addChild(title);
		this.addChild(head);
		this.addChild(body);
	}
	
	public Html(String bgcolor, String fontcolor) {
		this();
		body.addAttribute("BGCOLOR",bgcolor);
		body.addAttribute("TEXT",fontcolor);
		//body.addAttribute("LINK",fontcolor);
		//body.addAttribute("VLINK","black");
		//body.addAttribute("ALINK","black");
	}

	public void setTile(String title){
		this.title.addChild(new HtmlText(title));
	}
	
	public HtmlP addP(){
		HtmlP p = new HtmlP();
		this.addChild(p);
		return p;
	}

	public void addHr(){
		HtmlHr hr = new HtmlHr();
		this.addChild(hr);
	}
	
	public void addBr(){
		HtmlBr br = new HtmlBr();
		this.addChild(br);
	}
	
	public void addText(String text){
		HtmlText t = new HtmlText(text);
		this.addChild(t);
	}
	
	public HtmlA addA(String destination, String text){
		HtmlA anchor = new HtmlA(destination, text);
		this.addChild(anchor);
		return anchor;
	}
	
	public HtmlForm addForm(String target){
		HtmlForm form = new HtmlForm(target);
		this.addChild(form);
		return form;
	}
	
	public HtmlTable addTable(){
		HtmlTable table = new HtmlTable();
		this.addChild(table);
		return table;
	}
	
	public StringBuilder getPage(){
		return this.build();
	}

	public HtmlForm addForm(String target, String method) {
		HtmlForm form = new HtmlForm(target,method);
		this.addChild(form);
		return form;
	}
}
