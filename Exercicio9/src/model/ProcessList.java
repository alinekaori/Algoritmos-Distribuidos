package model;

import java.util.ArrayList;

public class ProcessList extends ArrayList<CustomProcess> {

	private static final long serialVersionUID = -2849036960731731638L;

	public CustomProcess getSelf(){
		for( CustomProcess p : this ){
			if( p.isSelf() ){
				return p;
			}
		}
		
		return null;
	}
	
	public CustomProcess getProcessById( int id ){
		for( CustomProcess p : this ){
			if( p.getId() == id ){
				return p;
			}
		}
		
		return null;
	}

	public CustomProcess getOther(){
		for( CustomProcess p : this ){
			if( !p.isSelf() ){
				return p;
			}
		}
		
		return null;
	}
	
	public CustomProcess selectLeader(){
		int minEpoch = Integer.MAX_VALUE;
		CustomProcess leader = null;
		
		for( CustomProcess p : this ){
			if( minEpoch > p.getEpoch() ){
				minEpoch = p.getEpoch();
				leader = p;
			}
		}
		
		return leader;
	}
	
	public void updateIfExists(CustomProcess source) {
		for(CustomProcess p : this){
			if(p.getId() == source.getId()){
				p.setEpoch(source.getEpoch());
				break;
			}
		}
	}
}