using UnityEngine;
using System.Collections;

public class CellManager : MonoBehaviour {
	private static CellManager _instance;

	public static CellManager Instance {
		get {
			if (_instance == null) {
				_instance = GameObject.FindObjectOfType<CellManager> ();
				DontDestroyOnLoad (_instance.gameObject);
			}
			return _instance;
		}
	}

	void Awake() {
		if (_instance == null) {
			_instance = this;
			DontDestroyOnLoad (this);
		} else {
			if (this != _instance) {
				Destroy (this.gameObject);
			}
		}
	}
		


	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
	
	}

	public RectTransform CellPanel = null;

	private GameObject _currentObject = null;

	public GameObject CurrentObject {
		get {
			return _currentObject;
		}
		set {
			_currentObject = value;
		}
	}

	private bool _menuOpened = false;

	public bool MenuOpened {
		get {
			return _menuOpened;
		}
		set {
			_menuOpened = value;
			CellPanel.gameObject.SetActive (_menuOpened);
		}
	}

	public void OpenCellPanelForObject(GameObject gameObject) {
		_currentObject = gameObject;

		MenuOpened = true;
	}

	public void CloseCellMenu() {
		if (MenuOpened)
			MenuOpened = false;
	}
}
