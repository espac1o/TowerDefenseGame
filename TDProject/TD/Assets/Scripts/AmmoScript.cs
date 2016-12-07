using UnityEngine;
using System.Collections;

public class AmmoScript : MonoBehaviour {

    public GameObject target;
    public float damage;
    public float speed;

	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void FixedUpdate () {

        Vector2 v = target.GetComponent<Transform>().position - gameObject.GetComponent<Transform>().position;
        if (v.magnitude < 0.1f){
			target.GetComponent<UnitScript> ().lostHpOn (damage);
            Destroy(gameObject);
        }
        v.Normalize();
        gameObject.GetComponent<Rigidbody2D>().velocity = v * speed;

        
	}
}
