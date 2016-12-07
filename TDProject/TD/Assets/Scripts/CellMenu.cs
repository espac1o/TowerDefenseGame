using UnityEngine;
using System.Collections;

public class CellMenu : MonoBehaviour {

	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
	
	}

	public void OnClick(string actionName) {
		switch(actionName) {
		case "tower1":
			buildTower ("tower1");
			break;
		}
	}

	private void buildTower(string towerType) {
		GameObject current = CellManager.Instance.CurrentObject;
		if (current != null) {
			// building tower

			CellManager.Instance.CloseCellMenu ();
		}
	}
}
