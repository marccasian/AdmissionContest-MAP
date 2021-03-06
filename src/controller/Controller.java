package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import domain.Candidat;
import domain.Sectie;
import domain.ValidatorException;

import utils.Observable;
import utils.Observer;

public class Controller implements Observable<Candidat>{
	private repository.RepoCandidatiSerializat _repoC;
	private repository.RepoSectiiFile _repoS;
	private domain.CandidateValidator cValidator;
	private domain.SectieValidator sValidator;
	protected List <Observer<Candidat>> observers = new ArrayList<Observer<Candidat>>();
	
	Predicate<Candidat> majori=c->{return c.getVarsta()>=18;};
    Predicate<Candidat> startWithC=c->c.getNume().startsWith("C");
    Predicate<Sectie> moreThan100 = s->{return s.getNrLoc()>=100;};
    Predicate<Sectie> startWithS = s->s.getNume().startsWith("S");
	
	public Controller(){
		_repoC = new repository.RepoCandidatiSerializat();
		_repoS = new repository.RepoSectiiFile("Sectii.txt");
		
		cValidator = new domain.CandidateValidator();
		sValidator = new domain.SectieValidator();
	}
	
	public void adaugaCandidat(Integer id, String nume, String tel,
			String adresa , Integer varsta) throws ValidatorException, sun.security.validator.ValidatorException{
		
		domain.Candidat cand = new domain.Candidat(id,nume,tel,adresa,varsta);
		cValidator.validateEntity(cand);
		this._repoC.add(cand);
	}

	public void adaugaSectie(Integer id, String nume, Integer nrLoc) throws sun.security.validator.ValidatorException, ValidatorException{
		domain.Sectie sect = new domain.Sectie(id,nume,nrLoc);
		sValidator.validateEntity(sect);
		this._repoS.add(sect);
	}
	
	public Iterable<Candidat> getCandidati(){
		return this._repoC.getAll();
	}
	
	public int getNrCandidati(){
		return this._repoC.getElemsNr();
	}
	
	public int getNrSectii(){
		return this._repoS.getElemsNr();
	}
	
	public Iterable<Sectie> getSectii(){
		return this._repoS.getAll();
	}
	
	public domain.Candidat stergeCandidat(int id){
		int pos = this._repoC.getPosId(id);
		Candidat c = this._repoC.delete(pos);
		return c;
	}
	
	public domain.Sectie stergeSectie(int id){
		int pos = this._repoS.getPosId(id); 
		Sectie s = this._repoS.delete(pos);
		return s;
	}
	
	public domain.Candidat getCandidat(int id){ 
		Candidat c = this._repoC.findOne(id); 
		return c;
	}
	
	public domain.Sectie getSectie(int id){ 
		Sectie s = this._repoS.findOne(id); 
		return s;
	}

	@SuppressWarnings("static-access")
	public void saveRepo() {
		_repoC.serializeCandidat((ArrayList<Candidat>) _repoC.getAll());
		_repoS.saveData();
	}
	
	public static <E> List<E> filtrare(List<E> list, Predicate<E> prd){
		List<E> filtrata = list
				.stream()
			    .filter(prd)
			    .collect(Collectors.toList());
		//Collections.sort(filtrata,(f1,f2)->-(int)(f1.getGreutate()-f2.getGreutate()));
		return filtrata;
	}
	
	public List<Candidat> filterCandidatiMajori(){
		List<Candidat> filt = filtrare((List<Candidat>)getCandidati(), majori);
		Collections.sort(filt,(f1,f2)->(int)(f1.getVarsta()-f2.getVarsta()));
		return filt;
	}
	
	public List<Candidat> filterCandidatiC(){
		List<Candidat> filt =  filtrare((List<Candidat>)getCandidati(), startWithC);
		Collections.sort(filt,(f1,f2)->(f1.getNume().compareTo(f2.getNume())));
		return filt;
	}
	
	public List<Sectie> filterSectii100(){
		List<Sectie> filt = filtrare((List<Sectie>)getSectii(), moreThan100);
		Collections.sort(filt,(f1,f2)->(int)(f1.getNrLoc()-f2.getNrLoc()));
		return filt;
	}
	
	public List<Sectie> filterSectiiS(){
		List<Sectie> filt = filtrare((List<Sectie>)getSectii(), startWithS);
		Collections.sort(filt,(f1,f2)->(f1.getNume().compareTo(f2.getNume())));
		return filt;
	}	
	
	@Override
	public void addObserver(Observer<Candidat> o) {
		observers.add(o);
	}

	@Override
	public void removeObserver(Observer<Candidat> o) {
		observers.remove(o);
	}

	@Override
	public void notifyObservers(){
		for(Observer<Candidat> o : observers){
			o.update(this);
		}
	}
	
	public Candidat updateC(Candidat t) throws ValidatorException, sun.security.validator.ValidatorException
	{
		cValidator.validateEntity(t);
		Candidat s = _repoC.update(t);
		if (s==null)
		{
			notifyObservers();
		}
		saveRepo();
		return s;
	}
	
	public Candidat saveC(Candidat t) throws ValidatorException, sun.security.validator.ValidatorException
	{
		cValidator.validateEntity(t);
		Candidat c= null;
		c = _repoC.save(t);
		if (c==null)
		{
			notifyObservers();
		}
		saveRepo();
		return c;
	}
	
	public Candidat deleteC(Candidat t) throws ValidatorException, sun.security.validator.ValidatorException
	{
		cValidator.validateEntity(t);
		Candidat c= null;
		int id = t.getId();
		int pos = _repoC.getPosId(id);
		c = _repoC.delete(pos);
		if (c!=null)
		{
			notifyObservers();
		}
		saveRepo();
		return c;
	}
	
	public Sectie updateS(Sectie t) throws ValidatorException, sun.security.validator.ValidatorException
	{
		sValidator.validateEntity(t);
		Sectie s = _repoS.update(t);
		if (s==null)
		{
			notifyObservers();
		}
		saveRepo();
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
		saveRepo();
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
		saveRepo();
		return c;
	}
}