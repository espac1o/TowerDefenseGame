using UnityEngine;
using System.Collections.Generic;

public class UnitScript : MonoBehaviour {

    public float speed = 2;
    public float hp = 10;
    public float damage = 1f;

	private Dictionary<int, int[]> route; // general route to nexus, get it from GeneratorScript
	private int CELL_SIZE; // const size, get it from GeneratorScript

	private int currStep; // or how much it went (amount of cells)
	private const float CELL_PASSING_TIME = 32; // time that needs "to pass" the cell
	private float currPassingTime;
	private Vector3 distance;
    
    // Use this for initialization
	void Start () {
		var generator = GameObject.Find ("GENERATOR");
		route = generator.GetComponent<GeneratorScript> ().roadRoute;
		CELL_SIZE = generator.GetComponent<GeneratorScript> ().CELL_SIZE;
		currStep = 0;
		currPassingTime = CELL_PASSING_TIME;
		distance = new Vector3 (route[0][1] * CELL_SIZE, route[0][0] * CELL_SIZE, 0);
	}
	
	// Update is called once per frame
	void FixedUpdate () {
		move ();
		//moveStep ();
	}

	void move() {
		if (currPassingTime <= 0) {
			currPassingTime = CELL_PASSING_TIME;
			currStep++;
		}

		if (currStep + 1 >= route.Count)
			return;
		
		//float delta = speed / CELL_PASSING_TIME;
		int[] currCell = new int[2];
		int[] nextCell = new int[2];
		currCell = route [currStep];
		nextCell = route [currStep + 1];

		//float dx = 1, dy = 1;
		int x1 = currCell [1] * CELL_SIZE;
		int y1 = currCell [0] * CELL_SIZE;

		int x2 = nextCell [1] * CELL_SIZE;
		int y2 = nextCell [0] * CELL_SIZE;
		/*
		if (gameObject.GetComponent<Transform>().position.x != x) {
			dx = delta;
		}
		if (gameObject.GetComponent<Transform>().position.y != y) {
			dy = delta;
		}
		
		Vector3 v = new Vector3 (x * dx, y * dy, 0);
		if (distance.Equals(new Vector3(0, 0, 0)))
			distance = v;
		else
			distance += v;
		*/
		distance += new Vector3 (x2 - x1, y2 - y1, 0) * speed / CELL_PASSING_TIME;
		gameObject.GetComponent<Transform>().position = distance;

		currPassingTime -= speed;
	}

	void moveStep() {
		if (currStep > 0)
			return;
		int[] nextCell = new int[2];
		nextCell = route [currStep];
		distance = new Vector3 (nextCell [1] * CELL_SIZE, nextCell [0] * CELL_SIZE, 0);
		gameObject.GetComponent<Transform>().position = distance;
		currStep++;
	}

	public void lostHpOn(float damage) {
		hp -= damage;
		if (hp <= 0) {
			Destroy (gameObject);
		}
	}
}
