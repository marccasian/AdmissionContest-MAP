package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import domain.Sectie;
import domain.ValidatorException;

import utils.Observable;
import utils.Observer;

public class ControllerSectie implements Observable<Sectie>{
	private repository.RepoSectiiXML _repoS;
	public repository.RepoSectiiXML get_repoS() {
		return _repoS;
	}

	public void set_repoS(repository.RepoSectiiXML _repoS) {
		this._repoS = _repoS;
	}

	private domain.SectieValidator sValidator;
	public domain.SectieValidator getsValidator() {
		return sValidator;
	}

	public void setsValidator(domain.SectieValidator sValidator) {
		this.sValidator = sValidator;
	}

	protected List <Observer<Sectie>> observers = new ArrayList<Observer<Sectie>>();
	
    Predicate<Sectie> moreThan100 = s->{return s.getNrLoc()>=100;};
    Predicate<Sectie> startWithS = s->s.getNume().startsWith("S");
	
	public ControllerSectie(){
	}
	
	public void adaugaSectie(Integer id, String nume, Integer nrLoc) throws sun.security.validator.ValidatorException, ValidatorException{
		domain.Sectie sect = new domain.Sectie(getNewID(),nume,nrLoc);
		sValidator.validateEntity(sect);
		this._repoS.add(sect);
	}
	
	public Integer getNewID() {
		int max = 0;
		for (Sectie c: this.getSectii()){
			if (c.getId() > max) max = c.getId();
		}
		return max+1;
	}

	
	public int getNrSectii(){
		return this._repoS.getElemsNr();
	}
	
	public Iterable<Sectie> getSectii(){
		return this._repoS.getAll();
	}
	
	public domain.Sectie stergeSectie(int id){
		int pos = this._repoS.getPosId(id); 
		Sectie s = this._repoS.delete(pos);
		return s;
	}
	
	public domain.Sectie getSectie(int id){ 
		Sectie s = this._repoS.findOne(id); 
		return s;
	}

	public void saveRepo() {
		_repoS.saveData();
	}
	
	public static <E> List<E> filtrare(List<E> list, Predicate<E> prd){
		List<E> filtrata = list
				.stream()
			    .filter(prd)
			    .collect(Collectors.toList());
		return filtrata;
	}
	
	public List<Sectie> filterSectii100(){
		List<Sectie> filt = filtrare((List<Sectie>)getSectii(), moreThan100);
		Collections.sort(filt,(f1,f2)->(int)(f1.getNrLoc()-f2.getNrLoc()));
		return filt;
	}
	
	public List<Sectie> filterSectiiNrLoc(Integer nr){
		Predicate<Sectie> moreThanNrLoc = s->{return s.getNrLoc()>=nr;};
		List<Sectie> filt = filtrare((List<Sectie>)getSectii(), moreThanNrLoc);
		Collections.sort(filt,(f1,f2)->(int)(f1.getNrLoc()-f2.getNrLoc()));
		return filt;
	}
	
	public List<Sectie> filterSectiiS(){
		List<Sectie> filt = filtrare((List<Sectie>)getSectii(), startWithS);
		Collections.sort(filt,(f1,f2)->(f1.getNume().compareTo(f2.getNume())));
		return filt;
	}	
	
	public List<Sectie> filterSectiiNume(String nu){
		Predicate<Sectie> numeFilter = s->s.getNume().contains(nu);
		List<Sectie> filt = filtrare((List<Sectie>)getSectii(), numeFilter);
		Collections.sort(filt,(f1,f2)->(f1.getNume().compareTo(f2.getNume())));
		return filt;
	}
	
	@Override
	public void addObserver(Observer<Sectie> o) {
		observers.add(o);
	}

	@Override
	public void removeObserver(Observer<Sectie> o) {
		observers.remove(o);
	}

	@Override
	public void notifyObservers(){
		for(Observer<Sectie> o : observers){
			o.update(this);
		}
	}
	
	public Sectie updateS(Sectie t) throws ValidatorException, sun.security.validator.ValidatorException
	{
		sValidator.validateEntity(t);
		Sectie s = _repoS.update(t);
		if (s==null)
		{
			notifyObservers();
		}
		return s;
	}
	
	public Sectie saveS(Sectie t) throws ValidatorException, sun.security.validator.ValidatorException
	{
		sValidator.validateEntity(t);
		Sectie c= null;
		c = _repoS.save(t);
		if (c==null)
		{
			notifyObservers();
		}
		return c;
	}
	
	public Sectie deleteS(Sectie t) throws ValidatorException, sun.security.validator.ValidatorException
	{
		sValidator.validateEntity(t);
		Sectie c= null;
		int id = t.getId();
		int pos = _repoS.getPosId(id);
		c = _repoS.delete(pos);
		if (c!=null)
		{
			notifyObservers();
		}
		return c;
	}
}
